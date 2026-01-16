// LAB 12 - REQUIREMENT 2: Unit tests for ChecklistItemStatus entity (constructor and functionality)
package com.uvt.newcomerassistan.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uvt.newcomerassistant.demo.ChecklistItemStatus;
import com.uvt.newcomerassistant.demo.User;

class ChecklistItemStatusTests {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123", "test@example.com");
        testUser.setId(1L);
    }

    @Test
    void testDefaultConstructor() {
        ChecklistItemStatus status = new ChecklistItemStatus();
        assertNotNull(status, "ChecklistItemStatus should be created with default constructor");
        assertNull(status.getId(), "ID should be null for new status");
        assertNull(status.getUser(), "User should be null for new status");
        assertNull(status.getChecklistItemId(), "Checklist item ID should be null for new status");
    }

    @Test
    void testParameterizedConstructor() {
        String itemId = "item-1";
        boolean isChecked = true;
        
        ChecklistItemStatus status = new ChecklistItemStatus(testUser, itemId, isChecked);
        
        assertNotNull(status, "ChecklistItemStatus should be created with parameterized constructor");
        assertEquals(testUser, status.getUser(), "User should match constructor parameter");
        assertEquals(itemId, status.getChecklistItemId(), "Item ID should match constructor parameter");
        assertEquals(isChecked, status.isChecked(), "Checked status should match constructor parameter");
        assertNotNull(status.getCheckedAt(), "CheckedAt timestamp should be set");
        assertNotNull(status.getCreatedAt(), "CreatedAt timestamp should be set");
    }

    @Test
    void testSetChecked() {
        ChecklistItemStatus status = new ChecklistItemStatus(testUser, "item-1", false);
        assertFalse(status.isChecked(), "Initial checked status should be false");
        
        status.setChecked(true);
        assertTrue(status.isChecked(), "Checked status should be updated to true");
    }

    @Test
    void testSetUser() {
        ChecklistItemStatus status = new ChecklistItemStatus();
        status.setUser(testUser);
        assertEquals(testUser, status.getUser(), "User should be set correctly");
    }

    @Test
    void testSetChecklistItemId() {
        ChecklistItemStatus status = new ChecklistItemStatus();
        String itemId = "item-123";
        status.setChecklistItemId(itemId);
        assertEquals(itemId, status.getChecklistItemId(), "Checklist item ID should be set correctly");
    }

    @Test
    void testInitializedAsUnchecked() {
        ChecklistItemStatus status = new ChecklistItemStatus(testUser, "item-2", false);
        assertFalse(status.isChecked(), "Status should be initialized as unchecked");
    }

    @Test
    void testInitializedAsChecked() {
        ChecklistItemStatus status = new ChecklistItemStatus(testUser, "item-3", true);
        assertTrue(status.isChecked(), "Status should be initialized as checked");
    }
}
