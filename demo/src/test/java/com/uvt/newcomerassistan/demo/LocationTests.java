// LAB 12 - REQUIREMENT 2: Unit tests for Location entity (constructor and functionality)
package com.uvt.newcomerassistan.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.uvt.newcomerassistant.demo.Location;

class LocationTests {

    @Test
    void testDefaultConstructor() {
        Location location = new Location();
        assertNotNull(location, "Location should be created with default constructor");
        assertNull(location.getName(), "Name should be null for new location");
        assertNull(location.getCategory(), "Category should be null for new location");
        assertNull(location.getDetails(), "Details should be null for new location");
    }

    @Test
    void testSetName() {
        Location location = new Location();
        String name = "City Hall";
        location.setName(name);
        assertEquals(name, location.getName(), "Name should be set correctly");
    }

    @Test
    void testSetCategory() {
        Location location = new Location();
        String category = "Government";
        location.setCategory(category);
        assertEquals(category, location.getCategory(), "Category should be set correctly");
    }

    @Test
    void testSetDetails() {
        Location location = new Location();
        String details = "Open 9-5 weekdays";
        location.setDetails(details);
        assertEquals(details, location.getDetails(), "Details should be set correctly");
    }

    @Test
    void testMatchesWithName() {
        Location location = new Location();
        location.setName("City Hall");
        location.setCategory("Government");
        location.setDetails("Open 9-5 weekdays");
        
        assertTrue(location.matches("city"), "Should match with name");
        assertTrue(location.matches("HALL"), "Should match with name (case insensitive)");
    }

    @Test
    void testMatchesWithCategory() {
        Location location = new Location();
        location.setName("City Hall");
        location.setCategory("Government");
        location.setDetails("Open 9-5 weekdays");
        
        assertTrue(location.matches("government"), "Should match with category");
        assertTrue(location.matches("GOVERNMENT"), "Should match with category (case insensitive)");
    }

    @Test
    void testMatchesWithDetails() {
        Location location = new Location();
        location.setName("City Hall");
        location.setCategory("Government");
        location.setDetails("Open 9-5 weekdays");
        
        assertTrue(location.matches("weekdays"), "Should match with details");
        assertTrue(location.matches("OPEN"), "Should match with details (case insensitive)");
    }

    @Test
    void testNoMatch() {
        Location location = new Location();
        location.setName("City Hall");
        location.setCategory("Government");
        location.setDetails("Open 9-5 weekdays");
        
        assertFalse(location.matches("shopping"), "Should not match with non-existing text");
    }
}
