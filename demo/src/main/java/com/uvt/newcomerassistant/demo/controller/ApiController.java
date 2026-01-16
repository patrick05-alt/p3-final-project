// LAB 12 - REQUIREMENT 6: Database connection and query data (GET endpoints)
// LAB 12 - REQUIREMENT 7: Insert/update data into DB (POST/PUT endpoints)
// LAB 12 - REQUIREMENT 9: Input validation
// LAB 12 - REQUIREMENT 11: REST endpoints for CRUD operations
package com.uvt.newcomerassistant.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvt.newcomerassistant.demo.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AppData appData;
    private final UserRepository userRepository;
    private final ChecklistItemStatusRepository checklistItemStatusRepository;
    private final AnnouncementRepository announcementRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public ApiController(
            AppData appData, 
            UserRepository userRepository, 
            ChecklistItemStatusRepository checklistItemStatusRepository,
            AnnouncementRepository announcementRepository,
            EventRepository eventRepository,
            LocationRepository locationRepository,
            ContactRepository contactRepository) {
        this.appData = appData;
        this.userRepository = userRepository;
        this.checklistItemStatusRepository = checklistItemStatusRepository;
        this.announcementRepository = announcementRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.contactRepository = contactRepository;
    }

    @GetMapping("/contacts")
    public List<Contact> getContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/announcements")
    public List<Announcement> getAnnouncements() {
        return announcementRepository.findAll();
    }

    @GetMapping("/checklist")
    public Object getChecklist() {
        return appData.getChecklistItems();
    }

    @GetMapping("/search")
    public List<Searchable> search(@RequestParam String q) {
        // Input validation
        if (q == null || q.trim().isEmpty()) {
            return List.of();
        }
        
        List<Searchable> results = new ArrayList<>();
        
        // Search events
        eventRepository.findAll().stream()
                .filter(item -> item.matches(q))
                .forEach(results::add);
        
        // Search locations
        locationRepository.findAll().stream()
                .filter(item -> item.matches(q))
                .forEach(results::add);
        
        // Search contacts
        contactRepository.findAll().stream()
                .filter(item -> item.matches(q))
                .forEach(results::add);
        
        return results;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Input validation
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return ResponseEntity.badRequest().build();
        }
        
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        // Input validation
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        // Input validation
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getUsername() != null && user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getEmail() != null && (user.getEmail().trim().isEmpty() || !user.getEmail().contains("@"))) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getPassword() != null && user.getPassword().length() < 6) {
            return ResponseEntity.badRequest().build();
        }
        
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (user.getUsername() != null) {
                        existingUser.setUsername(user.getUsername());
                    }
                    if (user.getEmail() != null) {
                        existingUser.setEmail(user.getEmail());
                    }
                    if (user.getPassword() != null) {
                        existingUser.setPassword(user.getPassword());
                    }
                    User updatedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Input validation
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users/{userId}/checklist/{itemId}/check")
    public ResponseEntity<ChecklistItemStatus> checkItem(@PathVariable Long userId, @PathVariable String itemId) {
        // Input validation
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        ChecklistItemStatus status = checklistItemStatusRepository
                .findByUserIdAndChecklistItemId(userId, itemId)
                .orElse(new ChecklistItemStatus(user, itemId, false));
        
        status.setChecked(true);
        ChecklistItemStatus saved = checklistItemStatusRepository.save(status);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/users/{userId}/checklist/{itemId}/uncheck")
    public ResponseEntity<ChecklistItemStatus> uncheckItem(@PathVariable Long userId, @PathVariable String itemId) {
        // Input validation
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        ChecklistItemStatus status = checklistItemStatusRepository
                .findByUserIdAndChecklistItemId(userId, itemId)
                .orElse(new ChecklistItemStatus(user, itemId, true));
        
        status.setChecked(false);
        ChecklistItemStatus saved = checklistItemStatusRepository.save(status);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/users/{userId}/checklist-progress")
    public ResponseEntity<ChecklistProgress> getChecklistProgress(@PathVariable Long userId) {
        // Input validation
        if (userId == null || userId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<ChecklistItemStatus> allItems = checklistItemStatusRepository.findByUserId(userId);
        List<ChecklistItemStatus> checkedItems = checklistItemStatusRepository.findByUserIdAndIsChecked(userId, true);
        
        ChecklistProgress progress = new ChecklistProgress(
                userId,
                checkedItems.size(),
                allItems.size()
        );
        return ResponseEntity.ok(progress);
    }

    // CRUD for ChecklistItemStatus
    @GetMapping("/checklist-status")
    public ResponseEntity<List<ChecklistItemStatus>> getAllChecklistStatuses() {
        List<ChecklistItemStatus> statuses = checklistItemStatusRepository.findAll();
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/checklist-status/{id}")
    public ResponseEntity<ChecklistItemStatus> getChecklistStatus(@PathVariable Long id) {
        // Input validation
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return checklistItemStatusRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/checklist-status")
    public ResponseEntity<ChecklistItemStatus> createChecklistStatus(@RequestBody ChecklistItemStatusRequest request) {
        // Input validation
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getChecklistItemId() == null || request.getChecklistItemId().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        ChecklistItemStatus status = new ChecklistItemStatus(user, request.getChecklistItemId(), request.isChecked());
        ChecklistItemStatus saved = checklistItemStatusRepository.save(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/checklist-status/{id}")
    public ResponseEntity<ChecklistItemStatus> updateChecklistStatus(@PathVariable Long id, @RequestBody ChecklistItemStatusRequest request) {
        // Input validation
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return checklistItemStatusRepository.findById(id)
                .map(status -> {
                    status.setChecked(request.isChecked());
                    ChecklistItemStatus updated = checklistItemStatusRepository.save(status);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/checklist-status/{id}")
    public ResponseEntity<Void> deleteChecklistStatus(@PathVariable Long id) {
        // Input validation
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        if (checklistItemStatusRepository.findById(id).isPresent()) {
            checklistItemStatusRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
