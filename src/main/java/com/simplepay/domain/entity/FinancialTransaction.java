package com.simplepay.domain.entity;

import com.simplepay.domain.enums.TransactionPartyType;
import com.simplepay.domain.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transactions")
public class FinancialTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "value", nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @NotNull
    @Column(name = "payer_id", nullable = false)
    private Long payer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payer_type", nullable = false, length = 20)
    private TransactionPartyType payerType;

    @NotNull
    @Column(name = "payee_id", nullable = false)
    private Long payee;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payee_type", nullable = false, length = 20)
    private TransactionPartyType payeeType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public FinancialTransaction() {}

    public FinancialTransaction(BigDecimal value, Long payer, TransactionPartyType payerType, Long payee, TransactionPartyType payeeType, TransactionStatus status) {
        this.value = value;
        this.payer = payer;
        this.payerType = payerType;
        this.payee = payee;
        this.payeeType = payeeType;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public Long getPayer() { return payer; }
    public void setPayer(Long payer) { this.payer = payer; }
    public TransactionPartyType getPayerType() { return payerType; }
    public void setPayerType(TransactionPartyType payerType) { this.payerType = payerType; }
    public Long getPayee() { return payee; }
    public void setPayee(Long payee) { this.payee = payee; }
    public TransactionPartyType getPayeeType() { return payeeType; }
    public void setPayeeType(TransactionPartyType payeeType) { this.payeeType = payeeType; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
