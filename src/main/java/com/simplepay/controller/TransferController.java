package com.simplepay.controller;

import com.simplepay.domain.enums.TransactionPartyType;
import com.simplepay.domain.enums.TransactionStatus;
import com.simplepay.domain.service.FinancialTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transfer")
@Tag(name = "Transfer", description = "Endpoint para transferências financeiras entre usuários e lojistas")
public class TransferController {

    @Autowired
    private FinancialTransactionService transactionService;

    @Operation(summary = "Realiza uma transferência financeira entre usuários/lojistas")
    @PostMapping
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionStatus status = transactionService.transfer(
            request.value(),
            request.payer(),
            request.payerType(),
            request.payee(),
            request.payeeType()
        );
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", getStatusMessage(status));
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
    
    private String getStatusMessage(TransactionStatus status) {
        switch (status) {
            case SUCCESS:
                return "Transferência realizada com sucesso";
            case PENDING:
                return "Transferência em processamento";
            case FAILED:
                return "Transferência falhou";
            case REFUNDED:
                return "Transferência estornada";
            default:
                return "Status desconhecido";
        }
    }

	public record TransferRequest(
		@NotNull(message = "Value is required")
		@Positive(message = "Value must be positive")
		BigDecimal value,

		@NotNull(message = "Payer ID is required")
		Long payer,

		@NotNull(message = "Payer type is required")
		TransactionPartyType payerType,

		@NotNull(message = "Payee ID is required")
		Long payee,

		@NotNull(message = "Payee type is required")
		TransactionPartyType payeeType
	) {}
}
