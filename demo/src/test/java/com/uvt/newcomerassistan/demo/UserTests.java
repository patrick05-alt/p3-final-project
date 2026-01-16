// LAB 12 - REQUIREMENT 2: Unit tests for User entity (constructor and functionality)
package com.uvt.newcomerassistan.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.uvt.newcomerassistant.demo.User;

class UserTests {

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNotNull(user, "User should be created with default constructor");
        assertNull(user.getId(), "ID should be null for new user");
        assertNull(user.getUsername(), "Username should be null for new user");
        assertNull(user.getEmail(), "Email should be null for new user");
        assertNull(user.getPassword(), "Password should be null for new user");
    }

    @Test
    void testParameterizedConstructor() {
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        
        User user = new User(username, password, email);
        
        assertNotNull(user, "User should be created with parameterized constructor");
        assertEquals(username, user.getUsername(), "Username should match constructor parameter");
        assertEquals(password, user.getPassword(), "Password should match constructor parameter");
        assertEquals(email, user.getEmail(), "Email should match constructor parameter");
    }

    @Test
    void testSetUsername() {
        User user = new User();
        String username = "newuser";
        user.setUsername(username);
        assertEquals(username, user.getUsername(), "Username should be set correctly");
    }

    @Test
    void testSetEmail() {
        User user = new User();
        String email = "new@example.com";
        user.setEmail(email);
        assertEquals(email, user.getEmail(), "Email should be set correctly");
    }

    @Test
    void testSetPassword() {
        User user = new User();
        String password = "newpassword";
        user.setPassword(password);
        assertEquals(password, user.getPassword(), "Password should be set correctly");
    }

    @Test
    void testUserWithAllFields() {
        User user = new User("john_doe", "secure123", "john@example.com");
        user.setId(1L);
        
        assertEquals(1L, user.getId(), "ID should be set correctly");
        assertEquals("john_doe", user.getUsername(), "Username should match");
        assertEquals("secure123", user.getPassword(), "Password should match");
        assertEquals("john@example.com", user.getEmail(), "Email should match");
    }

    @Test
    void testCheckedItemsInitialization() {
        User user = new User();
        assertNotNull(user.getCheckedItems(), "Checked items should be initialized");
        assertTrue(user.getCheckedItems().isEmpty(), "Checked items should be empty initially");
    }
}
