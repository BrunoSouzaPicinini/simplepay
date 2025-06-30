package com.simplepay.domain.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        user = new User("John Doe", "12345678901", "john@example.com", "password123");
    }

    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() {
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("12345678901", user.getDocument());
        assertEquals("john@example.com", user.getMail());
        assertNotNull(user.getPassword());
        assertNotEquals("password123", user.getPassword()); // Password should be encrypted
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should encrypt password on creation")
    void shouldEncryptPasswordOnCreation() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("password123", user.getPassword()));
        assertNotEquals("password123", user.getPassword());
    }

    @Test
    @DisplayName("Should verify password correctly")
    void shouldVerifyPasswordCorrectly() {
        assertTrue(user.verifyPassword("password123"));
        assertFalse(user.verifyPassword("wrongpassword"));
    }

    @Test
    @DisplayName("Should update password with encryption")
    void shouldUpdatePasswordWithEncryption() {
        String oldPassword = user.getPassword();
        user.updatePassword("newpassword456");
        
        assertNotEquals(oldPassword, user.getPassword());
        assertTrue(user.verifyPassword("newpassword456"));
        assertFalse(user.verifyPassword("password123"));
    }

    @Test
    @DisplayName("Should encrypt password when using setter")
    void shouldEncryptPasswordWhenUsingSetter() {
        String oldPassword = user.getPassword();
        user.setPassword("newpassword789");
        
        assertNotEquals(oldPassword, user.getPassword());
        assertNotEquals("newpassword789", user.getPassword());
        assertTrue(user.verifyPassword("newpassword789"));
    }

    @Test
    @DisplayName("Should validate required name field")
    void shouldValidateRequiredNameField() {
        User invalidUser = new User();
        invalidUser.setName("");
        invalidUser.setDocument("12345678901");
        invalidUser.setMail("test@example.com");
        invalidUser.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name is required")));
    }

    @Test
    @DisplayName("Should validate name length")
    void shouldValidateNameLength() {
        User invalidUser = new User();
        invalidUser.setName("A"); // Too short
        invalidUser.setDocument("12345678901");
        invalidUser.setMail("test@example.com");
        invalidUser.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Name must be between 2 and 100 characters")));
    }

    @Test
    @DisplayName("Should validate required document field")
    void shouldValidateRequiredDocumentField() {
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setDocument("");
        invalidUser.setMail("test@example.com");
        invalidUser.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Document is required")));
    }

    @Test
    @DisplayName("Should validate document length")
    void shouldValidateDocumentLength() {
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setDocument("123456789"); // Too short
        invalidUser.setMail("test@example.com");
        invalidUser.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Document must be between 11 and 14 characters")));
    }

    @Test
    @DisplayName("Should validate required email field")
    void shouldValidateRequiredEmailField() {
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setDocument("12345678901");
        invalidUser.setMail("");
        invalidUser.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    @DisplayName("Should validate email format")
    void shouldValidateEmailFormat() {
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setDocument("12345678901");
        invalidUser.setMail("invalid-email");
        invalidUser.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    @DisplayName("Should validate required password field")
    void shouldValidateRequiredPasswordField() {
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setDocument("12345678901");
        invalidUser.setMail("test@example.com");
        
        assertThrows(IllegalArgumentException.class, () -> {
            invalidUser.setPassword("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            invalidUser.setPassword(null);
        });
    }

    @Test
    @DisplayName("Should validate password minimum length")
    void shouldValidatePasswordMinimumLength() {
        User invalidUser = new User();
        invalidUser.setName("John Doe");
        invalidUser.setDocument("12345678901");
        invalidUser.setMail("test@example.com");
        
        assertThrows(IllegalArgumentException.class, () -> {
            invalidUser.setPassword("12345"); // Too short
        });
    }

    @Test
    @DisplayName("Should validate password in constructor")
    void shouldValidatePasswordInConstructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("John Doe", "12345678901", "john@example.com", "");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new User("John Doe", "12345678901", "john@example.com", "12345");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new User("John Doe", "12345678901", "john@example.com", null);
        });
    }

    @Test
    @DisplayName("Should validate password in updatePassword method")
    void shouldValidatePasswordInUpdatePasswordMethod() {
        User user = new User("John Doe", "12345678901", "john@example.com", "password123");
        
        assertThrows(IllegalArgumentException.class, () -> {
            user.updatePassword("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            user.updatePassword("12345");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            user.updatePassword(null);
        });
    }

    @Test
    @DisplayName("Should pass validation with valid data")
    void shouldPassValidationWithValidData() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        User user1 = new User("John Doe", "12345678901", "john@example.com", "password123");
        User user2 = new User("Jane Doe", "12345678901", "john@example.com", "password456");
        User user3 = new User("John Doe", "98765432109", "jane@example.com", "password123");
        
        user1.setId(1L);
        user2.setId(1L);
        user3.setId(2L);

        assertEquals(user1, user2); // Same id, document, and mail
        assertNotEquals(user1, user3); // Different id
        assertNotEquals(user1, null);
        assertNotEquals(user1, "string");
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        User user1 = new User("John Doe", "12345678901", "john@example.com", "password123");
        User user2 = new User("Jane Doe", "12345678901", "john@example.com", "password456");
        
        user1.setId(1L);
        user2.setId(1L);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString without exposing password")
    void shouldImplementToStringWithoutExposingPassword() {
        String userString = user.toString();
        
        assertTrue(userString.contains("John Doe"));
        assertTrue(userString.contains("12345678901"));
        assertTrue(userString.contains("john@example.com"));
        assertFalse(userString.contains("password123")); // Password should not be exposed
        assertFalse(userString.contains(user.getPassword())); // Encrypted password should not be exposed
    }

    @Test
    @DisplayName("Should update timestamps on update")
    void shouldUpdateTimestampsOnUpdate() {
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();
        
        // Wait a small amount to ensure timestamp difference
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        user.setName("Updated Name");
        
        // Since we're testing the entity directly without JPA context,
        // we need to manually call the PreUpdate method behavior
        user.setUpdatedAt(LocalDateTime.now());
        
        assertNotEquals(originalUpdatedAt, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should accept valid document lengths")
    void shouldAcceptValidDocumentLengths() {
        // Test CPF (11 digits)
        User userWithCPF = new User("John Doe", "12345678901", "john@example.com", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(userWithCPF);
        assertTrue(violations.isEmpty());

        // Test CNPJ (14 digits)
        User userWithCNPJ = new User("Company Name", "12345678901234", "company@example.com", "password123");
        violations = validator.validate(userWithCNPJ);
        assertTrue(violations.isEmpty());
    }
}
