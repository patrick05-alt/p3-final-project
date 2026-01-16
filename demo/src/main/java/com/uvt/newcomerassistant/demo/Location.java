package com.uvt.newcomerassistant.demo;

public class Location implements Searchable {
    private String name;
    private String category;
    private String details;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean matches(String query) {
        return name.toLowerCase().contains(query.toLowerCase()) ||
               category.toLowerCase().contains(query.toLowerCase()) ||
               details.toLowerCase().contains(query.toLowerCase());
    }
}
