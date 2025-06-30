package com.simplepay.domain.service;

import com.simplepay.domain.entity.User;
import com.simplepay.domain.entity.Seller;
import com.simplepay.domain.enums.TransactionPartyType;
import com.simplepay.domain.enums.TransactionStatus;
import com.simplepay.domain.exception.BusinessException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FinancialTransactionServiceUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private SellerService sellerService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private FinancialTransactionService subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should throw if payer not found")
    void shouldThrowIfPayerNotFound() {
        when(userService.findById(1L)).thenReturn(Optional.empty());
        when(sellerService.findById(1L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(BusinessException.class, () ->
            subject.transfer(BigDecimal.TEN, 1L, TransactionPartyType.USER, 2L, TransactionPartyType.USER)
        );
        assertTrue(ex.getMessage().contains("Payer not found"));
    }

    @Test
    @DisplayName("Should throw if payee not found")
    void shouldThrowIfPayeeNotFound() {
        User payer = mock(User.class);
        when(userService.findById(1L)).thenReturn(Optional.of(payer));
        when(sellerService.findById(1L)).thenReturn(Optional.empty());
        when(userService.findById(2L)).thenReturn(Optional.empty());
        when(sellerService.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(BusinessException.class, () ->
            subject.transfer(BigDecimal.TEN, 1L, TransactionPartyType.USER, 2L, TransactionPartyType.USER)
        );
        assertTrue(ex.getMessage().contains("Payee not found"));
    }

    @Test
    @DisplayName("Should throw if payer is seller")
    void shouldThrowIfPayerIsSeller() {
        Seller seller = mock(Seller.class);
        when(userService.findById(1L)).thenReturn(Optional.empty());
        when(sellerService.findById(1L)).thenReturn(Optional.of(seller));
        when(userService.findById(2L)).thenReturn(Optional.of(mock(User.class)));
        Exception ex = assertThrows(BusinessException.class, () ->
            subject.transfer(BigDecimal.TEN, 1L, TransactionPartyType.SELLER, 2L, TransactionPartyType.USER)
        );
        assertTrue(ex.getMessage().contains("Lojistas não podem enviar transferências"));
    }

    @Test
    @DisplayName("Should throw if payer has insufficient balance")
    void shouldThrowIfPayerHasInsufficientBalance() {
        User payer = mock(User.class);
        when(userService.findById(1L)).thenReturn(Optional.of(payer));
        when(sellerService.findById(1L)).thenReturn(Optional.empty());
        when(userService.findById(2L)).thenReturn(Optional.of(mock(User.class)));
        when(payer.hasBalance(any())).thenReturn(false);
        Exception ex = assertThrows(BusinessException.class, () ->
            subject.transfer(BigDecimal.TEN, 1L, TransactionPartyType.USER, 2L, TransactionPartyType.USER)
        );
        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
    }

    @Test
    @DisplayName("Should throw if not authorized by external service")
    void shouldThrowIfNotAuthorizedByExternalService() {
        User payer = mock(User.class);
        when(userService.findById(1L)).thenReturn(Optional.of(payer));
        when(sellerService.findById(1L)).thenReturn(Optional.empty());
        when(userService.findById(2L)).thenReturn(Optional.of(mock(User.class)));
        when(payer.hasBalance(any())).thenReturn(true);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Negado");
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
            .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        Exception ex = assertThrows(BusinessException.class, () ->
            subject.transfer(BigDecimal.TEN, 1L, TransactionPartyType.USER, 2L, TransactionPartyType.USER)
        );
        assertTrue(ex.getMessage().contains("Transação não autorizada"));
    }

    @Test
    @DisplayName("Should succeed for valid transfer between users")
    void shouldSucceedForValidTransferBetweenUsers() {
        User payer = mock(User.class);
        User payee = mock(User.class);
        when(userService.findById(1L)).thenReturn(Optional.of(payer));
        when(sellerService.findById(1L)).thenReturn(Optional.empty());
        when(userService.findById(2L)).thenReturn(Optional.of(payee));
        when(sellerService.findById(2L)).thenReturn(Optional.empty());
        when(payer.hasBalance(any())).thenReturn(true);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Autorizado");
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
            .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        TransactionStatus status = subject.transfer(BigDecimal.TEN, 1L, TransactionPartyType.USER, 2L, TransactionPartyType.USER);
        assertEquals(TransactionStatus.SUCCESS, status);
        verify(userService).save(payer);
        verify(userService).save(payee);
    }
}
