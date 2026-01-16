// LAB 12 - REQUIREMENT 5: Database entity - SharedChecklist
package com.uvt.newcomerassistant.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shared_checklists", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"owner_id", "shared_with_id"})
})
public class SharedChecklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @ManyToOne
    @JoinColumn(name = "shared_with_id", nullable = false)
    private User sharedWith;
    
    @Column(nullable = false)
    private LocalDateTime sharedDate;
    
    @Column(nullable = false)
    private boolean canEdit = false;
    
    public SharedChecklist() {
        this.sharedDate = LocalDateTime.now();
    }
    
    public SharedChecklist(User owner, User sharedWith, boolean canEdit) {
        this.owner = owner;
        this.sharedWith = sharedWith;
        this.canEdit = canEdit;
        this.sharedDate = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public User getSharedWith() {
        return sharedWith;
    }
    
    public void setSharedWith(User sharedWith) {
        this.sharedWith = sharedWith;
    }
    
    public LocalDateTime getSharedDate() {
        return sharedDate;
    }
    
    public void setSharedDate(LocalDateTime sharedDate) {
        this.sharedDate = sharedDate;
    }
    
    public boolean isCanEdit() {
        return canEdit;
    }
    
    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
