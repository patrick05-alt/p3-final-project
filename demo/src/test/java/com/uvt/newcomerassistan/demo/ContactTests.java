// LAB 12 - REQUIREMENT 2: Unit tests for Contact entity (constructor and functionality)
package com.uvt.newcomerassistan.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.uvt.newcomerassistant.demo.Contact;

class ContactTests {

    @Test
    void testDefaultConstructor() {
        Contact contact = new Contact();
        assertNotNull(contact, "Contact should be created with default constructor");
        assertNull(contact.getName(), "Name should be null for new contact");
        assertNull(contact.getEmail(), "Email should be null for new contact");
        assertNull(contact.getPhone(), "Phone should be null for new contact");
    }

    @Test
    void testSetName() {
        Contact contact = new Contact();
        String name = "John Doe";
        contact.setName(name);
        assertEquals(name, contact.getName(), "Name should be set correctly");
    }

    @Test
    void testSetEmail() {
        Contact contact = new Contact();
        String email = "john@example.com";
        contact.setEmail(email);
        assertEquals(email, contact.getEmail(), "Email should be set correctly");
    }

    @Test
    void testSetPhone() {
        Contact contact = new Contact();
        String phone = "123-456-7890";
        contact.setPhone(phone);
        assertEquals(phone, contact.getPhone(), "Phone should be set correctly");
    }

    @Test
    void testMatchesWithName() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact.setEmail("john@example.com");
        contact.setPhone("123-456-7890");
        
        assertTrue(contact.matches("john"), "Should match with name");
        assertTrue(contact.matches("JOHN"), "Should match with name (case insensitive)");
    }

    @Test
    void testMatchesWithEmail() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact.setEmail("john@example.com");
        contact.setPhone("123-456-7890");
        
        assertTrue(contact.matches("example"), "Should match with email");
        assertTrue(contact.matches("EXAMPLE"), "Should match with email (case insensitive)");
    }

    @Test
    void testMatchesWithPhone() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact.setEmail("john@example.com");
        contact.setPhone("123-456-7890");
        
        assertTrue(contact.matches("123"), "Should match with phone");
        assertTrue(contact.matches("456"), "Should match with phone");
    }

    @Test
    void testNoMatch() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact.setEmail("john@example.com");
        contact.setPhone("123-456-7890");
        
        assertFalse(contact.matches("xyz"), "Should not match with non-existing text");
    }
}
