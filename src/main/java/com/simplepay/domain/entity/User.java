package com.simplepay.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "document"),
    @UniqueConstraint(columnNames = "mail")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Document is required")
    @Size(min = 11, max = 14, message = "Document must be between 11 and 14 characters")
    @Column(name = "document", nullable = false, unique = true, length = 14)
    private String document;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "mail", nullable = false, unique = true, length = 100)
    private String mail;

    @NotBlank(message = "Password is required")
    @Column(name = "user_password", nullable = false)
    private String password;

    @NotNull(message = "Balance is required")
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String name, String document, String mail, String password) {
        validatePlainPassword(password);
        this.name = name;
        this.document = document;
        this.mail = mail;
        this.password = encryptPassword(password);
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public User(String name, String document, String mail, String password, BigDecimal balance) {
        validatePlainPassword(password);
        this.name = name;
        this.document = document;
        this.mail = mail;
        this.password = encryptPassword(password);
        this.balance = balance != null ? balance : BigDecimal.ZERO;
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

    private String encryptPassword(String plainPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(plainPassword);
    }

    private void validatePlainPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    public boolean verifyPassword(String plainPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(plainPassword, this.password);
    }

    public void updatePassword(String newPassword) {
        validatePlainPassword(newPassword);
        this.password = encryptPassword(newPassword);
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        validatePlainPassword(password);
        this.password = encryptPassword(password);
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    public void addToBalance(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void subtractFromBalance(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            if (this.balance.compareTo(amount) >= 0) {
                this.balance = this.balance.subtract(amount);
                this.updatedAt = LocalDateTime.now();
            } else {
                throw new IllegalArgumentException("Insufficient balance");
            }
        }
    }

    public boolean hasBalance(BigDecimal amount) {
        return amount != null && this.balance.compareTo(amount) >= 0;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(document, user.document) &&
               Objects.equals(mail, user.mail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, document, mail);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", document='" + document + '\'' +
                ", mail='" + mail + '\'' +
                ", balance=" + balance +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
