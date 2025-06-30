package com.simplepay.domain.entity;

import com.simplepay.domain.enums.TransactionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transaction_history")
public class FinancialTransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private FinancialTransaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", nullable = false, length = 20)
    private TransactionStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private TransactionStatus newStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "note", length = 255)
    private String note;

    public FinancialTransactionHistory() {}

    public FinancialTransactionHistory(FinancialTransaction transaction, TransactionStatus oldStatus, TransactionStatus newStatus, String note) {
        this.transaction = transaction;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = LocalDateTime.now();
        this.note = note;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FinancialTransaction getTransaction() { return transaction; }
    public void setTransaction(FinancialTransaction transaction) { this.transaction = transaction; }
    public TransactionStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(TransactionStatus oldStatus) { this.oldStatus = oldStatus; }
    public TransactionStatus getNewStatus() { return newStatus; }
    public void setNewStatus(TransactionStatus newStatus) { this.newStatus = newStatus; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
