package com.uvt.newcomerassistant.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AppData {
    private List<Event> allEvents;
    private Map<String, Contact> allContacts;
    private List<Location> allLocations;
    private List<ChecklistItem> checklistItems;

    public List<Event> getAllEvents() {
        return allEvents == null ? Collections.emptyList() : allEvents;
    }

    public void setAllEvents(List<Event> allEvents) {
        this.allEvents = allEvents;
    }

    public Map<String, Contact> getAllContacts() {
        return allContacts == null ? Collections.emptyMap() : allContacts;
    }

    public void setAllContacts(Map<String, Contact> allContacts) {
        this.allContacts = allContacts;
    }

    public List<Location> getAllLocations() {
        return allLocations == null ? Collections.emptyList() : allLocations;
    }

    public void setAllLocations(List<Location> allLocations) {
        this.allLocations = allLocations;
    }

    public List<ChecklistItem> getChecklistItems() {
        return checklistItems == null ? Collections.emptyList() : checklistItems;
    }

    public void setChecklistItems(List<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public List<Searchable> getSearchableItems() {
        List<Searchable> searchableItems = new ArrayList<>();
        searchableItems.addAll(getAllContacts().values());
        searchableItems.addAll(getAllEvents());
        searchableItems.addAll(getAllLocations());
        return searchableItems;
    }
}
