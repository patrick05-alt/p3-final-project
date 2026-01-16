package com.uvt.newcomerassistant.demo.controller;

import com.uvt.newcomerassistant.demo.AppData;
import com.uvt.newcomerassistant.demo.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AppData appData;

    @Autowired
    public ApiController(AppData appData) {
        this.appData = appData;
    }

    @GetMapping("/contacts")
    public Object getContacts() {
        return appData.getAllContacts();
    }

    @GetMapping("/events")
    public Object getEvents() {
        return appData.getAllEvents();
    }

    @GetMapping("/locations")
    public Object getLocations() {
        return appData.getAllLocations();
    }

    @GetMapping("/checklist")
    public Object getChecklist() {
        return appData.getChecklistItems();
    }

    @GetMapping("/search")
    public List<Searchable> search(@RequestParam String q) {
        return appData.getSearchableItems()
                .stream()
                .filter(s -> s.matches(q))
                .collect(Collectors.toList());
    }
}
