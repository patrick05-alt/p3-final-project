package com.uvt.newcomerassistant.demo;

import java.time.LocalDate;

public class Event implements Searchable {
    private String name;
    private LocalDate date;
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean matches(String query) {
        return name.toLowerCase().contains(query.toLowerCase()) ||
               location.toLowerCase().contains(query.toLowerCase());
    }
}
