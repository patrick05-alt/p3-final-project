// LAB 12 - REQUIREMENT 5: Database entity - User
// LAB 12 - REQUIREMENT 8: User roles for different access levels
package com.uvt.newcomerassistant.demo;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role = "USER"; // USER or ADMIN

    private String firstName;
    private String lastName;
    private String phone;
    private String country;
    private String city;
    private String bio;
    private String profilePicture;
    private String preferredLanguage = "en"; // en, es, fr
    private String theme = "light"; // light, dark
    
    @Column(nullable = false)
    private boolean emailNotifications = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChecklistItemStatus> checkedItems = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventRSVP> eventRSVPs = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FavoriteLocation> favoriteLocations = new HashSet<>();

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<ChecklistItemStatus> getCheckedItems() {
        return checkedItems;
    }

    public void setCheckedItems(Set<ChecklistItemStatus> checkedItems) {
        this.checkedItems = checkedItems;
    }

    public void addCheckedItem(ChecklistItemStatus item) {
        this.checkedItems.add(item);
        item.setUser(this);
    }

    public void removeCheckedItem(ChecklistItemStatus item) {
        this.checkedItems.remove(item);
        item.setUser(null);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Set<EventRSVP> getEventRSVPs() {
        return eventRSVPs;
    }

    public void setEventRSVPs(Set<EventRSVP> eventRSVPs) {
        this.eventRSVPs = eventRSVPs;
    }

    public Set<FavoriteLocation> getFavoriteLocations() {
        return favoriteLocations;
    }

    public void setFavoriteLocations(Set<FavoriteLocation> favoriteLocations) {
        this.favoriteLocations = favoriteLocations;
    }
}
