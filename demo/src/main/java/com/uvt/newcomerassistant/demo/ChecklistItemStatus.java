// LAB 12 - REQUIREMENT 5: Database entity - ChecklistItemStatus
package com.uvt.newcomerassistant.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "checklist_item_status")
public class ChecklistItemStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String checklistItemId;

    @Column(nullable = false)
    private boolean isChecked;

    @Column(nullable = false)
    private LocalDateTime checkedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChecklistItemStatus() {
    }

    public ChecklistItemStatus(User user, String checklistItemId, boolean isChecked) {
        this.user = user;
        this.checklistItemId = checklistItemId;
        this.isChecked = isChecked;
        this.checkedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getChecklistItemId() {
        return checklistItemId;
    }

    public void setChecklistItemId(String checklistItemId) {
        this.checklistItemId = checklistItemId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        this.checkedAt = LocalDateTime.now();
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
