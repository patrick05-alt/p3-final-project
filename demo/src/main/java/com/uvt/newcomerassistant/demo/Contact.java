// LAB 12 - REQUIREMENT 5: Database entity - Contact
package com.uvt.newcomerassistant.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "contacts")
public class Contact implements Searchable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
