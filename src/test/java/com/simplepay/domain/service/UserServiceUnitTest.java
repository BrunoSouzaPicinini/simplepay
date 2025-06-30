package com.simplepay.domain.service;

import com.simplepay.domain.entity.User;
import com.simplepay.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "12345678901", "john@example.com", "password123");
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void shouldReturnEmptyWhenUserNotFoundById() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999L);

        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find user by document")
    void shouldFindUserByDocument() {
        when(userRepository.findByDocument("12345678901")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByDocument("12345678901");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository).findByDocument("12345678901");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        when(userRepository.findByMail("john@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByMail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository).findByMail("john@example.com");
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        User anotherUser = new User("Jane Doe", "98765432100", "jane@example.com", "password456");
        List<User> users = Arrays.asList(testUser, anotherUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should check if user exists by document")
    void shouldCheckIfUserExistsByDocument() {
        when(userRepository.existsByDocument("12345678901")).thenReturn(true);

        boolean result = userService.existsByDocument("12345678901");

        assertTrue(result);
        verify(userRepository).existsByDocument("12345678901");
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        when(userRepository.existsByMail("john@example.com")).thenReturn(true);

        boolean result = userService.existsByMail("john@example.com");

        assertTrue(result);
        verify(userRepository).existsByMail("john@example.com");
    }

    @Test
    @DisplayName("Should check if user exists by document or email")
    void shouldCheckIfUserExistsByDocumentOrEmail() {
        when(userRepository.existsByDocumentOrMail("12345678901", "john@example.com")).thenReturn(true);

        boolean result = userService.existsByDocumentOrMail("12345678901", "john@example.com");

        assertTrue(result);
        verify(userRepository).existsByDocumentOrMail("12345678901", "john@example.com");
    }

    @Test
    @DisplayName("Should create new user successfully")
    void shouldCreateNewUserSuccessfully() {
        when(userRepository.existsByDocumentOrMail("12345678901", "john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.create("John Doe", "12345678901", "john@example.com", "password123");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository).existsByDocumentOrMail("12345678901", "john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing document or email")
    void shouldThrowExceptionWhenCreatingUserWithExistingDocumentOrEmail() {
        when(userRepository.existsByDocumentOrMail("12345678901", "john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.create("John Doe", "12345678901", "john@example.com", "password123");
        });

        verify(userRepository).existsByDocumentOrMail("12345678901", "john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should save new user successfully")
    void shouldSaveNewUserSuccessfully() {
        User newUser = new User("Jane Doe", "98765432100", "jane@example.com", "password456");
        when(userRepository.existsByDocumentOrMail("98765432100", "jane@example.com")).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = userService.save(newUser);

        assertNotNull(result);
        verify(userRepository).existsByDocumentOrMail("98765432100", "jane@example.com");
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("Should throw exception when saving user with existing document or email")
    void shouldThrowExceptionWhenSavingUserWithExistingDocumentOrEmail() {
        User newUser = new User("Jane Doe", "12345678901", "john@example.com", "password456");
        when(userRepository.existsByDocumentOrMail("12345678901", "john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.save(newUser);
        });

        verify(userRepository).existsByDocumentOrMail("12345678901", "john@example.com");
        verify(userRepository, never()).save(newUser);
    }

    @Test
    @DisplayName("Should update existing user successfully")
    void shouldUpdateExistingUserSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.update(testUser);

        assertNotNull(result);
        verify(userRepository).existsById(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.update(testUser);
        });

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating user with existing document")
    void shouldThrowExceptionWhenUpdatingUserWithExistingDocument() {
        User updatedUser = new User("John Doe", "99999999999", "john@example.com", "password123");
        updatedUser.setId(1L);
        
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByDocument("99999999999")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.update(updatedUser);
        });

        verify(userRepository).existsById(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).existsByDocument("99999999999");
        verify(userRepository, never()).save(updatedUser);
    }

    @Test
    @DisplayName("Should delete user by ID successfully")
    void shouldDeleteUserByIdSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteById(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user by ID")
    void shouldThrowExceptionWhenDeletingNonExistentUserById() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteById(999L);
        });

        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(999L);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        userService.delete(testUser);

        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should count users")
    void shouldCountUsers() {
        when(userRepository.count()).thenReturn(5L);

        long result = userService.count();

        assertEquals(5L, result);
        verify(userRepository).count();
    }

    @Test
    void givenExistingUserId_whenFindById_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Optional<User> result = userService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    void givenNonExistentUserId_whenFindById_thenReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<User> result = userService.findById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void givenValidUserData_whenCreate_thenReturnSavedUser() {
        when(userRepository.existsByDocumentOrMail(anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        User result = userService.create("John Doe", "12345678901", "john@example.com", "password123");
        assertNotNull(result);
        assertEquals(testUser.getMail(), result.getMail());
    }

    @Test
    void givenDuplicateDocumentOrEmail_whenCreate_thenThrowException() {
        when(userRepository.existsByDocumentOrMail(anyString(), anyString())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.create("User", "123", "mail", "pass"));
    }
}
