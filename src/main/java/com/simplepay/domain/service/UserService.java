package com.simplepay.domain.service;

import com.simplepay.domain.entity.User;
import com.simplepay.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByDocument(String document) {
        return userRepository.findByDocument(document);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean existsByDocument(String document) {
        return userRepository.existsByDocument(document);
    }

    @Transactional(readOnly = true)
    public boolean existsByMail(String mail) {
        return userRepository.existsByMail(mail);
    }

    @Transactional(readOnly = true)
    public boolean existsByDocumentOrMail(String document, String mail) {
        return userRepository.existsByDocumentOrMail(document, mail);
    }

    public User save(User user) {
        validateUserUniqueness(user);
        return userRepository.save(user);
    }

    public User create(String name, String document, String mail, String password) {
        if (existsByDocumentOrMail(document, mail)) {
            throw new IllegalArgumentException("User with this document or email already exists");
        }
        
        User user = new User(name, document, mail, password);
        return userRepository.save(user);
    }

    public User create(String name, String document, String mail, String password, BigDecimal balance) {
        if (existsByDocumentOrMail(document, mail)) {
            throw new IllegalArgumentException("User with this document or email already exists");
        }
        
        User user = new User(name, document, mail, password, balance);
        return userRepository.save(user);
    }

    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Check if document or email is being changed and if they're still unique
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            User existing = existingUser.get();
            
            // If document changed, check uniqueness
            if (!existing.getDocument().equals(user.getDocument()) && 
                userRepository.existsByDocument(user.getDocument())) {
                throw new IllegalArgumentException("Document already exists");
            }
            
            // If email changed, check uniqueness
            if (!existing.getMail().equals(user.getMail()) && 
                userRepository.existsByMail(user.getMail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
        
        return userRepository.save(user);
    }

    public Optional<User> update(Long id, User user) {
        if (!userRepository.existsById(id)) {
            return Optional.empty();
        }
        user.setId(id);
        return Optional.of(userRepository.save(user));
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }

    public User addToBalance(Long userId, BigDecimal amount) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = optionalUser.get();
        user.addToBalance(amount);
        return userRepository.save(user);
    }

    public User subtractFromBalance(Long userId, BigDecimal amount) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = optionalUser.get();
        user.subtractFromBalance(amount);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean hasBalance(Long userId, BigDecimal amount) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        return optionalUser.get().hasBalance(amount);
    }

    private void validateUserUniqueness(User user) {
        // For new users (no ID)
        if (user.getId() == null) {
            if (existsByDocumentOrMail(user.getDocument(), user.getMail())) {
                throw new IllegalArgumentException("User with this document or email already exists");
            }
        } else {
            // For existing users, check if document/email changed and is unique
            Optional<User> existingUser = userRepository.findById(user.getId());
            if (existingUser.isPresent()) {
                User existing = existingUser.get();
                
                if (!existing.getDocument().equals(user.getDocument()) && 
                    userRepository.existsByDocument(user.getDocument())) {
                    throw new IllegalArgumentException("Document already exists");
                }
                
                if (!existing.getMail().equals(user.getMail()) && 
                    userRepository.existsByMail(user.getMail())) {
                    throw new IllegalArgumentException("Email already exists");
                }
            }
        }
    }
}
