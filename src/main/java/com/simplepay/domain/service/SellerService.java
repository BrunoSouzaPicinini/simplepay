package com.simplepay.domain.service;

import com.simplepay.domain.entity.Seller;
import com.simplepay.domain.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Seller> findById(Long id) {
        return sellerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Seller> findByDocument(String document) {
        return sellerRepository.findByDocument(document);
    }

    @Transactional(readOnly = true)
    public Optional<Seller> findByEmail(String email) {
        return sellerRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean existsByDocument(String document) {
        return sellerRepository.existsByDocument(document);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return sellerRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByDocumentOrEmail(String document, String email) {
        return sellerRepository.existsByDocumentOrEmail(document, email);
    }

    public Seller save(Seller seller) {
        validateSellerUniqueness(seller);
        return sellerRepository.save(seller);
    }

    public Seller create(String name, String document, String email, String password) {
        if (existsByDocumentOrEmail(document, email)) {
            throw new IllegalArgumentException("Seller with this document or email already exists");
        }
        
        Seller seller = new Seller(name, document, email, password);
        return sellerRepository.save(seller);
    }

    public Seller create(String name, String document, String email, String password, BigDecimal balance) {
        if (existsByDocumentOrEmail(document, email)) {
            throw new IllegalArgumentException("Seller with this document or email already exists");
        }
        
        Seller seller = new Seller(name, document, email, password, balance);
        return sellerRepository.save(seller);
    }

    public Seller update(Seller seller) {
        if (!sellerRepository.existsById(seller.getId())) {
            throw new IllegalArgumentException("Seller not found");
        }
        
        Optional<Seller> existingSeller = sellerRepository.findById(seller.getId());
        if (existingSeller.isPresent()) {
            Seller existing = existingSeller.get();
            
            if (!existing.getDocument().equals(seller.getDocument()) && 
                sellerRepository.existsByDocument(seller.getDocument())) {
                throw new IllegalArgumentException("Document already exists");
            }
            
            if (!existing.getEmail().equals(seller.getEmail()) && 
                sellerRepository.existsByEmail(seller.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        
        return sellerRepository.save(seller);
    }

    public Optional<Seller> update(Long id, Seller seller) {
        if (!sellerRepository.existsById(id)) {
            return Optional.empty();
        }
        seller.setId(id);
        return Optional.of(sellerRepository.save(seller));
    }

    public void deleteById(Long id) {
        if (!sellerRepository.existsById(id)) {
            throw new IllegalArgumentException("Seller not found");
        }
        sellerRepository.deleteById(id);
    }

    public boolean delete(Long id) {
        if (!sellerRepository.existsById(id)) {
            return false;
        }
        sellerRepository.deleteById(id);
        return true;
    }

    public void delete(Seller seller) {
        sellerRepository.delete(seller);
    }

    @Transactional(readOnly = true)
    public long count() {
        return sellerRepository.count();
    }

    public Seller addToBalance(Long sellerId, BigDecimal amount) {
        Optional<Seller> optionalSeller = sellerRepository.findById(sellerId);
        if (optionalSeller.isEmpty()) {
            throw new IllegalArgumentException("Seller not found");
        }
        
        Seller seller = optionalSeller.get();
        seller.addToBalance(amount);
        return sellerRepository.save(seller);
    }

    private void validateSellerUniqueness(Seller seller) {
        if (seller.getId() == null) {
            if (existsByDocumentOrEmail(seller.getDocument(), seller.getEmail())) {
                throw new IllegalArgumentException("Seller with this document or email already exists");
            }
        } else {
            Optional<Seller> existingSeller = sellerRepository.findById(seller.getId());
            if (existingSeller.isPresent()) {
                Seller existing = existingSeller.get();
                
                if (!existing.getDocument().equals(seller.getDocument()) && 
                    sellerRepository.existsByDocument(seller.getDocument())) {
                    throw new IllegalArgumentException("Document already exists");
                }
                
                if (!existing.getEmail().equals(seller.getEmail()) && 
                    sellerRepository.existsByEmail(seller.getEmail())) {
                    throw new IllegalArgumentException("Email already exists");
                }
            }
        }
    }
}
