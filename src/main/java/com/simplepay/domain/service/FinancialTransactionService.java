package com.simplepay.domain.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.simplepay.domain.entity.FinancialTransaction;
import com.simplepay.domain.entity.FinancialTransactionHistory;
import com.simplepay.domain.entity.Seller;
import com.simplepay.domain.entity.User;
import com.simplepay.domain.enums.TransactionPartyType;
import com.simplepay.domain.enums.TransactionStatus;
import com.simplepay.domain.exception.BusinessException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class FinancialTransactionService {
    @Autowired
    private UserService userService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private RestTemplate restTemplate;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public TransactionStatus transfer(BigDecimal value, Long payerId, TransactionPartyType payerType, Long payeeId, TransactionPartyType payeeType) {
        validateParties(payerId, payerType, payeeId, payeeType);
        Optional<User> payerUser = payerType == TransactionPartyType.USER ? userService.findById(payerId) : Optional.empty();
        Optional<Seller> payerSeller = payerType == TransactionPartyType.SELLER ? sellerService.findById(payerId) : Optional.empty();
        Optional<User> payeeUser = payeeType == TransactionPartyType.USER ? userService.findById(payeeId) : Optional.empty();
        Optional<Seller> payeeSeller = payeeType == TransactionPartyType.SELLER ? sellerService.findById(payeeId) : Optional.empty();
        if (payerType == TransactionPartyType.SELLER) {
            throw new BusinessException("Lojistas não podem enviar transferências");
        }
        User payer = payerUser.get();
        validateBalance(payer, value);
        authorizeExternal();
        performTransfer(payer, value, payeeType, payeeUser, payeeSeller);
        FinancialTransaction transaction = saveTransactionAndHistory(value, payerId, payerType, payeeId, payeeType);
        updateTransactionToSuccess(transaction);
        notifyPayee(payeeType, payeeUser, payeeSeller, value);
        return TransactionStatus.SUCCESS;
    }

    private void validateParties(Long payerId, TransactionPartyType payerType, Long payeeId, TransactionPartyType payeeType) {
        if (payerType == null || payeeType == null) {
            throw new BusinessException("payerType e payeeType são obrigatórios");
        }
        Optional<User> payerUser = payerType == TransactionPartyType.USER ? userService.findById(payerId) : Optional.empty();
        Optional<Seller> payerSeller = payerType == TransactionPartyType.SELLER ? sellerService.findById(payerId) : Optional.empty();
        Optional<User> payeeUser = payeeType == TransactionPartyType.USER ? userService.findById(payeeId) : Optional.empty();
        Optional<Seller> payeeSeller = payeeType == TransactionPartyType.SELLER ? sellerService.findById(payeeId) : Optional.empty();
        if ((payerType == TransactionPartyType.USER && payerUser.isEmpty()) || (payerType == TransactionPartyType.SELLER && payerSeller.isEmpty())) {
            throw new BusinessException("Payer not found");
        }
        if ((payeeType == TransactionPartyType.USER && payeeUser.isEmpty()) || (payeeType == TransactionPartyType.SELLER && payeeSeller.isEmpty())) {
            throw new BusinessException("Payee not found");
        }
    }

    private void validateBalance(User payer, BigDecimal value) {
        if (!payer.hasBalance(value)) {
            throw new BusinessException("Saldo insuficiente");
        }
    }

    private void authorizeExternal() {
        String authorizeUrl = "https://util.devi.tools/api/v2/authorize";
        ResponseEntity<Map> authResponse = restTemplate.getForEntity(authorizeUrl, Map.class);
        if (authResponse.getStatusCode() != HttpStatus.OK ||
            authResponse.getBody() == null ||
            !"Autorizado".equalsIgnoreCase(String.valueOf(authResponse.getBody().get("message")))) {
            throw new BusinessException("Transação não autorizada pelo serviço externo");
        }
    }

    private void performTransfer(User payer, BigDecimal value, TransactionPartyType payeeType, Optional<User> payeeUser, Optional<Seller> payeeSeller) {
        payer.subtractFromBalance(value);
        userService.save(payer);
        if (payeeType == TransactionPartyType.USER) {
            User payee = payeeUser.get();
            payee.addToBalance(value);
            userService.save(payee);
        } else {
            Seller payee = payeeSeller.get();
            payee.addToBalance(value);
            sellerService.save(payee);
        }
    }

    private FinancialTransaction saveTransactionAndHistory(BigDecimal value, Long payerId, TransactionPartyType payerType, Long payeeId, TransactionPartyType payeeType) {
        FinancialTransaction transaction = new FinancialTransaction(
            value, payerId, payerType, payeeId, payeeType, TransactionStatus.PENDING
        );
        entityManager.persist(transaction);
        FinancialTransactionHistory pendingHistory = new FinancialTransactionHistory(
            transaction, null, TransactionStatus.PENDING, "Transação iniciada"
        );
        entityManager.persist(pendingHistory);
        return transaction;
    }

    private void updateTransactionToSuccess(FinancialTransaction transaction) {
        transaction.setStatus(TransactionStatus.SUCCESS);
        entityManager.merge(transaction);
        FinancialTransactionHistory successHistory = new FinancialTransactionHistory(
            transaction, TransactionStatus.PENDING, TransactionStatus.SUCCESS, "Transferência realizada com sucesso"
        );
        entityManager.persist(successHistory);
    }

    private void notifyPayee(TransactionPartyType payeeType, Optional<User> payeeUser, Optional<Seller> payeeSeller, BigDecimal value) {
        try {
            String notifyUrl = "https://util.devi.tools/api/v1/notify";
            Map<String, Object> notifyPayload = new HashMap<>();
            notifyPayload.put("to", payeeType == TransactionPartyType.USER ? payeeUser.get().getMail() : payeeSeller.get().getEmail());
            notifyPayload.put("message", "Você recebeu uma transferência de R$ " + value);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notifyPayload, headers);
            restTemplate.postForEntity(notifyUrl, request, String.class);
        } catch (Exception e) {
            // Log notification failure but don't fail the transaction
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}
