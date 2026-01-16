// LAB 12 - REQUIREMENT 2: Unit tests for Event entity (constructor and functionality)
package com.uvt.newcomerassistan.demo;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.uvt.newcomerassistant.demo.Event;

class EventTests {

    @Test
    void testDefaultConstructor() {
        Event event = new Event();
        assertNotNull(event, "Event should be created with default constructor");
        assertNull(event.getName(), "Name should be null for new event");
        assertNull(event.getDate(), "Date should be null for new event");
        assertNull(event.getLocation(), "Location should be null for new event");
    }

    @Test
    void testSetName() {
        Event event = new Event();
        String name = "Conference 2026";
        event.setName(name);
        assertEquals(name, event.getName(), "Name should be set correctly");
    }

    @Test
    void testSetDate() {
        Event event = new Event();
        LocalDate date = LocalDate.of(2026, 3, 15);
        event.setDate(date);
        assertEquals(date, event.getDate(), "Date should be set correctly");
    }

    @Test
    void testSetLocation() {
        Event event = new Event();
        String location = "Grand Hall";
        event.setLocation(location);
        assertEquals(location, event.getLocation(), "Location should be set correctly");
    }

    @Test
    void testMatchesWithName() {
        Event event = new Event();
        event.setName("Spring Conference");
        event.setLocation("Grand Hall");
        event.setDate(LocalDate.of(2026, 3, 15));
        
        assertTrue(event.matches("spring"), "Should match with name");
        assertTrue(event.matches("CONFERENCE"), "Should match with name (case insensitive)");
    }

    @Test
    void testMatchesWithLocation() {
        Event event = new Event();
        event.setName("Spring Conference");
        event.setLocation("Grand Hall");
        event.setDate(LocalDate.of(2026, 3, 15));
        
        assertTrue(event.matches("grand"), "Should match with location");
        assertTrue(event.matches("HALL"), "Should match with location (case insensitive)");
    }

    @Test
    void testNoMatch() {
        Event event = new Event();
        event.setName("Spring Conference");
        event.setLocation("Grand Hall");
        event.setDate(LocalDate.of(2026, 3, 15));
        
        assertFalse(event.matches("summer"), "Should not match with non-existing text");
    }
}
