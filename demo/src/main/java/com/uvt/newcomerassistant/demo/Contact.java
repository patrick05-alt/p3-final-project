package com.uvt.newcomerassistant.demo;

public class Contact implements Searchable {
    private String name;
    private String email;
    private String phone;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean matches(String query) {
        return name.toLowerCase().contains(query.toLowerCase()) ||
               email.toLowerCase().contains(query.toLowerCase()) ||
               phone.toLowerCase().contains(query.toLowerCase());
    }
}
