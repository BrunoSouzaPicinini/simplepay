package com.simplepay.domain.repository;

import com.simplepay.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "12345678901", "john@example.com", "password123");
    }

    @Test
    @DisplayName("Should save and find user by ID")
    void shouldSaveAndFindUserById() {
        User savedUser = userRepository.save(testUser);
        
        assertNotNull(savedUser.getId());
        
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("12345678901", foundUser.get().getDocument());
        assertEquals("john@example.com", foundUser.get().getMail());
    }

    @Test
    @DisplayName("Should find user by document")
    void shouldFindUserByDocument() {
        userRepository.save(testUser);
        
        Optional<User> foundUser = userRepository.findByDocument("12345678901");
        
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getMail());
    }

    @Test
    @DisplayName("Should return empty when user not found by document")
    void shouldReturnEmptyWhenUserNotFoundByDocument() {
        Optional<User> foundUser = userRepository.findByDocument("99999999999");
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        userRepository.save(testUser);
        
        Optional<User> foundUser = userRepository.findByMail("john@example.com");
        
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("12345678901", foundUser.get().getDocument());
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        Optional<User> foundUser = userRepository.findByMail("notfound@example.com");
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should check if user exists by document")
    void shouldCheckIfUserExistsByDocument() {
        userRepository.save(testUser);
        
        assertTrue(userRepository.existsByDocument("12345678901"));
        assertFalse(userRepository.existsByDocument("99999999999"));
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        userRepository.save(testUser);
        
        assertTrue(userRepository.existsByMail("john@example.com"));
        assertFalse(userRepository.existsByMail("notfound@example.com"));
    }

    @Test
    @DisplayName("Should find user by document or email")
    void shouldFindUserByDocumentOrEmail() {
        userRepository.save(testUser);
        
        // Find by document
        Optional<User> foundByDocument = userRepository.findByDocumentOrMail("12345678901", "different@email.com");
        assertTrue(foundByDocument.isPresent());
        assertEquals("John Doe", foundByDocument.get().getName());
        
        // Find by email
        Optional<User> foundByEmail = userRepository.findByDocumentOrMail("99999999999", "john@example.com");
        assertTrue(foundByEmail.isPresent());
        assertEquals("John Doe", foundByEmail.get().getName());
        
        // Find by both (should return the same user)
        Optional<User> foundByBoth = userRepository.findByDocumentOrMail("12345678901", "john@example.com");
        assertTrue(foundByBoth.isPresent());
        assertEquals("John Doe", foundByBoth.get().getName());
    }

    @Test
    @DisplayName("Should return empty when user not found by document or email")
    void shouldReturnEmptyWhenUserNotFoundByDocumentOrEmail() {
        Optional<User> foundUser = userRepository.findByDocumentOrMail("99999999999", "notfound@example.com");
        
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should check if user exists by document or email")
    void shouldCheckIfUserExistsByDocumentOrEmail() {
        userRepository.save(testUser);
        
        // Exists by document
        assertTrue(userRepository.existsByDocumentOrMail("12345678901", "different@email.com"));
        
        // Exists by email
        assertTrue(userRepository.existsByDocumentOrMail("99999999999", "john@example.com"));
        
        // Exists by both
        assertTrue(userRepository.existsByDocumentOrMail("12345678901", "john@example.com"));
        
        // Not exists
        assertFalse(userRepository.existsByDocumentOrMail("99999999999", "notfound@example.com"));
    }

    @Test
    @DisplayName("Should enforce unique constraint on document")
    void shouldEnforceUniqueConstraintOnDocument() {
        User user1 = new User("User One", "12345678901", "user1@example.com", "password123");
        User user2 = new User("User Two", "12345678901", "user2@example.com", "password456");
        
        userRepository.save(user1);
        
        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Should enforce unique constraint on email")
    void shouldEnforceUniqueConstraintOnEmail() {
        User user1 = new User("User One", "12345678901", "same@example.com", "password123");
        User user2 = new User("User Two", "98765432100", "same@example.com", "password456");
        
        userRepository.save(user1);
        
        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();
        
        assertTrue(userRepository.existsById(userId));
        
        userRepository.delete(savedUser);
        
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    @DisplayName("Should count users")
    void shouldCountUsers() {
        assertEquals(0, userRepository.count());
        
        userRepository.save(testUser);
        assertEquals(1, userRepository.count());
        
        User anotherUser = new User("Jane Doe", "98765432100", "jane@example.com", "password456");
        userRepository.save(anotherUser);
        assertEquals(2, userRepository.count());
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        assertTrue(userRepository.findAll().isEmpty());
        
        userRepository.save(testUser);
        User anotherUser = new User("Jane Doe", "98765432100", "jane@example.com", "password456");
        userRepository.save(anotherUser);
        
        assertEquals(2, userRepository.findAll().size());
    }
}
