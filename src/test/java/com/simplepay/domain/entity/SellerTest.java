package com.simplepay.domain.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SellerTest {

    @Test
    void shouldCreateSellerWithEncryptedPassword() {
        String plainPassword = "password123";
        Seller seller = new Seller("John's Store", "12345678000123", "store@example.com", plainPassword);

        assertNotNull(seller.getName());
        assertEquals("John's Store", seller.getName());
        assertEquals("12345678000123", seller.getDocument());
        assertEquals("store@example.com", seller.getEmail());
        assertNotEquals(plainPassword, seller.getPassword());
        assertEquals(BigDecimal.ZERO, seller.getBalance());
        assertNotNull(seller.getCreatedAt());
        assertNotNull(seller.getUpdatedAt());
    }

    @Test
    void shouldCreateSellerWithInitialBalance() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        Seller seller = new Seller("Store", "12345678000123", "store@example.com", "password123", initialBalance);

        assertEquals(initialBalance, seller.getBalance());
    }

    @Test
    void shouldEncryptPasswordWhenSettingNewPassword() {
        Seller seller = new Seller();
        String plainPassword = "newPassword123";
        
        seller.setPassword(plainPassword);
        
        assertNotEquals(plainPassword, seller.getPassword());
        assertTrue(seller.verifyPassword(plainPassword));
    }

    @Test
    void shouldVerifyCorrectPassword() {
        String plainPassword = "correctPassword";
        Seller seller = new Seller("Store", "12345678000123", "store@example.com", plainPassword);

        assertTrue(seller.verifyPassword(plainPassword));
        assertFalse(seller.verifyPassword("wrongPassword"));
    }

    @Test
    void shouldUpdatePasswordWithEncryption() {
        Seller seller = new Seller("Store", "12345678000123", "store@example.com", "oldPassword");
        String newPassword = "newSecurePassword";

        seller.updatePassword(newPassword);

        assertTrue(seller.verifyPassword(newPassword));
        assertFalse(seller.verifyPassword("oldPassword"));
    }

    @Test
    void shouldAddToBalance() {
        Seller seller = new Seller("Store", "12345678000123", "store@example.com", "password123");
        BigDecimal amount = new BigDecimal("500.00");

        seller.addToBalance(amount);

        assertEquals(amount, seller.getBalance());
    }

    @Test
    void shouldSubtractFromBalance() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        Seller seller = new Seller("Store", "12345678000123", "store@example.com", "password123", initialBalance);
        BigDecimal amountToSubtract = new BigDecimal("300.00");

        seller.subtractFromBalance(amountToSubtract);

        assertEquals(new BigDecimal("700.00"), seller.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenSubtractingMoreThanBalance() {
        BigDecimal initialBalance = new BigDecimal("100.00");
        Seller seller = new Seller("Store", "12345678000123", "store@example.com", "password123", initialBalance);
        BigDecimal amountToSubtract = new BigDecimal("200.00");

        assertThrows(IllegalArgumentException.class, () -> {
            seller.subtractFromBalance(amountToSubtract);
        });
    }

    @Test
    void shouldCheckIfHasBalance() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        Seller seller = new Seller("Store", "12345678000123", "store@example.com", "password123", initialBalance);

        assertTrue(seller.hasBalance(new BigDecimal("500.00")));
        assertTrue(seller.hasBalance(new BigDecimal("1000.00")));
        assertFalse(seller.hasBalance(new BigDecimal("1500.00")));
    }

    @Test
    void shouldReturnTrueForEqualSellers() {
        Seller seller1 = new Seller("Store", "12345678000123", "store@example.com", "password");
        Seller seller2 = new Seller("Store", "12345678000123", "store@example.com", "password");
        
        seller1.setId(1L);
        seller2.setId(1L);

        assertEquals(seller1, seller2);
        assertEquals(seller1.hashCode(), seller2.hashCode());
    }

    @Test
    void shouldReturnFalseForDifferentSellers() {
        Seller seller1 = new Seller("Store 1", "12345678000123", "store1@example.com", "password");
        Seller seller2 = new Seller("Store 2", "98765432000123", "store2@example.com", "password");
        
        seller1.setId(1L);
        seller2.setId(2L);

        assertNotEquals(seller1, seller2);
    }

    @Test
    void shouldNotExposePasswordInToString() {
        Seller seller = new Seller("Test Store", "12345678000123", "test@example.com", "secretPassword");
        String sellerString = seller.toString();

        assertFalse(sellerString.contains("secretPassword"));
        assertTrue(sellerString.contains("Test Store"));
        assertTrue(sellerString.contains("12345678000123"));
        assertTrue(sellerString.contains("test@example.com"));
    }

    @Test
    void shouldUseDefaultConstructor() {
        Seller seller = new Seller();
        
        assertNull(seller.getId());
        assertNull(seller.getName());
        assertNull(seller.getDocument());
        assertNull(seller.getEmail());
        assertNull(seller.getPassword());
        assertNull(seller.getBalance());
        assertNull(seller.getCreatedAt());
        assertNull(seller.getUpdatedAt());
    }

    @Test
    void shouldValidatePasswordInConstructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Seller("Store", "12345678000123", "store@example.com", "");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Seller("Store", "12345678000123", "store@example.com", "12345");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Seller("Store", "12345678000123", "store@example.com", null);
        });
    }

    @Test
    void shouldEncryptPasswordUsingBCrypt() {
        String plainPassword = "testPassword123";
        Seller seller = new Seller("Test Store", "12345678000123", "test@example.com", plainPassword);
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(plainPassword, seller.getPassword()));
    }

    @Test
    void shouldSetBalanceToZeroWhenNull() {
        Seller seller = new Seller();
        seller.setBalance(null);
        
        assertEquals(BigDecimal.ZERO, seller.getBalance());
    }
}
