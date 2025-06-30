package com.simplepay.domain.service;

import com.simplepay.domain.entity.Seller;
import com.simplepay.domain.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Seller Service Tests")
class SellerServiceUnitTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller testSeller;

    @BeforeEach
    void setUp() {
        testSeller = new Seller("John's Store", "12345678000123", "store@example.com", "password123");
        testSeller.setId(1L);
    }

    @Test
    @DisplayName("Should find seller by ID")
    void shouldFindSellerById() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));

        Optional<Seller> result = sellerService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("John's Store", result.get().getName());
        verify(sellerRepository).findById(1L);
    }

    @Test
    void givenExistingSellerId_whenFindById_thenReturnSeller() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
        Optional<Seller> result = sellerService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(testSeller.getId(), result.get().getId());
    }

    @Test
    void givenNonExistentSellerId_whenFindById_thenReturnEmpty() {
        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Seller> result = sellerService.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should create new seller successfully")
    void shouldCreateNewSellerSuccessfully() {
        when(sellerRepository.existsByDocumentOrEmail("12345678000123", "store@example.com")).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        Seller result = sellerService.create("John's Store", "12345678000123", "store@example.com", "password123");

        assertNotNull(result);
        assertEquals("John's Store", result.getName());
        verify(sellerRepository).existsByDocumentOrEmail("12345678000123", "store@example.com");
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void givenValidSellerData_whenCreate_thenReturnSavedSeller() {
        when(sellerRepository.existsByDocumentOrEmail(anyString(), anyString())).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);
        Seller result = sellerService.create("John's Store", "12345678000123", "store@example.com", "password123");
        assertNotNull(result);
        assertEquals(testSeller.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when creating seller with existing document or email")
    void shouldThrowExceptionWhenCreatingSellerWithExistingDocumentOrEmail() {
        when(sellerRepository.existsByDocumentOrEmail("12345678000123", "store@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            sellerService.create("John's Store", "12345678000123", "store@example.com", "password123");
        });

        verify(sellerRepository).existsByDocumentOrEmail("12345678000123", "store@example.com");
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void givenDuplicateDocumentOrEmail_whenCreate_thenThrowException() {
        when(sellerRepository.existsByDocumentOrEmail(anyString(), anyString())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> sellerService.create("Store", "123", "mail", "pass"));
    }

    @Test
    @DisplayName("Should add to seller balance")
    void shouldAddToSellerBalance() {
        BigDecimal initialBalance = new BigDecimal("500.00");
        BigDecimal amountToAdd = new BigDecimal("200.00");
        testSeller.setBalance(initialBalance);
        
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(testSeller)).thenReturn(testSeller);

        Seller result = sellerService.addToBalance(1L, amountToAdd);

        assertEquals(new BigDecimal("700.00"), result.getBalance());
        verify(sellerRepository).findById(1L);
        verify(sellerRepository).save(testSeller);
    }

    @Test
    void givenValidSellerIdAndAmount_whenAddToBalance_thenBalanceIsIncreased() {
        testSeller.setBalance(new BigDecimal("100.00"));
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(testSeller)).thenReturn(testSeller);
        Seller result = sellerService.addToBalance(1L, new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), result.getBalance());
    }

    @Test
    @DisplayName("Should throw exception when adding to balance of non-existent seller")
    void shouldThrowExceptionWhenAddingToBalanceOfNonExistentSeller() {
        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            sellerService.addToBalance(999L, new BigDecimal("100.00"));
        });

        verify(sellerRepository).findById(999L);
        verify(sellerRepository, never()).save(any(Seller.class));
    }
}
