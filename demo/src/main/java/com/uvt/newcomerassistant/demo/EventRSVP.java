// LAB 12 - REQUIREMENT 5: Database entity - EventRSVP
package com.uvt.newcomerassistant.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_rsvps", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "user_id"})
})
public class EventRSVP {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String status; // ATTENDING, MAYBE, NOT_ATTENDING
    
    @Column(nullable = false)
    private LocalDateTime rsvpDate;
    
    public EventRSVP() {
        this.rsvpDate = LocalDateTime.now();
    }
    
    public EventRSVP(Event event, User user, String status) {
        this.event = event;
        this.user = user;
        this.status = status;
        this.rsvpDate = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getRsvpDate() {
        return rsvpDate;
    }
    
    public void setRsvpDate(LocalDateTime rsvpDate) {
        this.rsvpDate = rsvpDate;
    }
}
