package com.simplepay.domain.repository;

import com.simplepay.domain.entity.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Seller Repository Tests")
class SellerRepositoryIntegrationTest {

    @Autowired
    private SellerRepository sellerRepository;

    private Seller testSeller;

    @BeforeEach
    void setUp() {
        testSeller = new Seller("John's Store", "12345678000123", "store@example.com", "password123");
    }

    @Test
    @DisplayName("Should save and find seller by ID")
    void shouldSaveAndFindSellerById() {
        Seller savedSeller = sellerRepository.save(testSeller);
        
        assertNotNull(savedSeller.getId());
        
        Optional<Seller> foundSeller = sellerRepository.findById(savedSeller.getId());
        
        assertTrue(foundSeller.isPresent());
        assertEquals("John's Store", foundSeller.get().getName());
        assertEquals("12345678000123", foundSeller.get().getDocument());
        assertEquals("store@example.com", foundSeller.get().getEmail());
    }

    @Test
    @DisplayName("Should find seller by document")
    void shouldFindSellerByDocument() {
        sellerRepository.save(testSeller);
        
        Optional<Seller> foundSeller = sellerRepository.findByDocument("12345678000123");
        
        assertTrue(foundSeller.isPresent());
        assertEquals("John's Store", foundSeller.get().getName());
        assertEquals("store@example.com", foundSeller.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when seller not found by document")
    void shouldReturnEmptyWhenSellerNotFoundByDocument() {
        Optional<Seller> foundSeller = sellerRepository.findByDocument("99999999999999");
        
        assertFalse(foundSeller.isPresent());
    }

    @Test
    @DisplayName("Should find seller by email")
    void shouldFindSellerByEmail() {
        sellerRepository.save(testSeller);
        
        Optional<Seller> foundSeller = sellerRepository.findByEmail("store@example.com");
        
        assertTrue(foundSeller.isPresent());
        assertEquals("John's Store", foundSeller.get().getName());
        assertEquals("12345678000123", foundSeller.get().getDocument());
    }

    @Test
    @DisplayName("Should return empty when seller not found by email")
    void shouldReturnEmptyWhenSellerNotFoundByEmail() {
        Optional<Seller> foundSeller = sellerRepository.findByEmail("notfound@example.com");
        
        assertFalse(foundSeller.isPresent());
    }

    @Test
    @DisplayName("Should check if seller exists by document")
    void shouldCheckIfSellerExistsByDocument() {
        sellerRepository.save(testSeller);
        
        assertTrue(sellerRepository.existsByDocument("12345678000123"));
        assertFalse(sellerRepository.existsByDocument("99999999999999"));
    }

    @Test
    @DisplayName("Should check if seller exists by email")
    void shouldCheckIfSellerExistsByEmail() {
        sellerRepository.save(testSeller);
        
        assertTrue(sellerRepository.existsByEmail("store@example.com"));
        assertFalse(sellerRepository.existsByEmail("notfound@example.com"));
    }

    @Test
    @DisplayName("Should find seller by document or email")
    void shouldFindSellerByDocumentOrEmail() {
        sellerRepository.save(testSeller);
        
        Optional<Seller> foundByDocument = sellerRepository.findByDocumentOrEmail("12345678000123", "different@email.com");
        assertTrue(foundByDocument.isPresent());
        assertEquals("John's Store", foundByDocument.get().getName());
        
        Optional<Seller> foundByEmail = sellerRepository.findByDocumentOrEmail("99999999999999", "store@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals("John's Store", foundByEmail.get().getName());
        
        Optional<Seller> foundByBoth = sellerRepository.findByDocumentOrEmail("12345678000123", "store@example.com");
        assertTrue(foundByBoth.isPresent());
        assertEquals("John's Store", foundByBoth.get().getName());
    }

    @Test
    @DisplayName("Should return empty when seller not found by document or email")
    void shouldReturnEmptyWhenSellerNotFoundByDocumentOrEmail() {
        Optional<Seller> foundSeller = sellerRepository.findByDocumentOrEmail("99999999999999", "notfound@example.com");
        
        assertFalse(foundSeller.isPresent());
    }

    @Test
    @DisplayName("Should check if seller exists by document or email")
    void shouldCheckIfSellerExistsByDocumentOrEmail() {
        sellerRepository.save(testSeller);
        
        assertTrue(sellerRepository.existsByDocumentOrEmail("12345678000123", "different@email.com"));
        assertTrue(sellerRepository.existsByDocumentOrEmail("99999999999999", "store@example.com"));
        assertTrue(sellerRepository.existsByDocumentOrEmail("12345678000123", "store@example.com"));
        assertFalse(sellerRepository.existsByDocumentOrEmail("99999999999999", "notfound@example.com"));
    }

    @Test
    @DisplayName("Should enforce unique constraint on document")
    void shouldEnforceUniqueConstraintOnDocument() {
        Seller seller1 = new Seller("Store One", "12345678000123", "store1@example.com", "password123");
        Seller seller2 = new Seller("Store Two", "12345678000123", "store2@example.com", "password456");
        
        sellerRepository.save(seller1);
        
        assertThrows(Exception.class, () -> {
            sellerRepository.saveAndFlush(seller2);
        });
    }

    @Test
    @DisplayName("Should enforce unique constraint on email")
    void shouldEnforceUniqueConstraintOnEmail() {
        Seller seller1 = new Seller("Store One", "12345678000123", "same@example.com", "password123");
        Seller seller2 = new Seller("Store Two", "98765432000123", "same@example.com", "password456");
        
        sellerRepository.save(seller1);
        
        assertThrows(Exception.class, () -> {
            sellerRepository.saveAndFlush(seller2);
        });
    }

    @Test
    @DisplayName("Should delete seller")
    void shouldDeleteSeller() {
        Seller savedSeller = sellerRepository.save(testSeller);
        Long sellerId = savedSeller.getId();
        
        assertTrue(sellerRepository.existsById(sellerId));
        
        sellerRepository.delete(savedSeller);
        
        assertFalse(sellerRepository.existsById(sellerId));
    }

    @Test
    @DisplayName("Should count sellers")
    void shouldCountSellers() {
        assertEquals(0, sellerRepository.count());
        
        sellerRepository.save(testSeller);
        assertEquals(1, sellerRepository.count());
        
        Seller anotherSeller = new Seller("Another Store", "98765432000123", "another@example.com", "password456");
        sellerRepository.save(anotherSeller);
        assertEquals(2, sellerRepository.count());
    }

    @Test
    @DisplayName("Should find all sellers")
    void shouldFindAllSellers() {
        assertTrue(sellerRepository.findAll().isEmpty());
        
        sellerRepository.save(testSeller);
        Seller anotherSeller = new Seller("Another Store", "98765432000123", "another@example.com", "password456");
        sellerRepository.save(anotherSeller);
        
        assertEquals(2, sellerRepository.findAll().size());
    }

    @Test
    @DisplayName("Should save seller with balance")
    void shouldSaveSellerWithBalance() {
        BigDecimal initialBalance = new BigDecimal("1000.00");
        Seller sellerWithBalance = new Seller("Rich Store", "12345678000123", "rich@example.com", "password123", initialBalance);
        
        Seller savedSeller = sellerRepository.save(sellerWithBalance);
        
        assertEquals(initialBalance, savedSeller.getBalance());
    }
}
