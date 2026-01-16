// LAB 12 - REQUIREMENT 6: Database connection and query data (checklist status, shared checklists)
// LAB 12 - REQUIREMENT 7: Insert/update data into DB (share checklist, update status)
// LAB 12 - REQUIREMENT 9: Input validation
// LAB 12 - REQUIREMENT 11: REST endpoints for checklist management
package com.uvt.newcomerassistant.demo.controller;

import com.uvt.newcomerassistant.demo.*;
import com.uvt.newcomerassistant.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

@RestController
@RequestMapping("/api/checklist")
public class ChecklistController {

    @Autowired
    private SharedChecklistRepository sharedChecklistRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChecklistItemStatusRepository checklistStatusRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping("/share")
    public ResponseEntity<?> shareChecklist(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody ShareRequest request,
                                           HttpServletRequest httpRequest) {
        User owner = getUserFromToken(authHeader);
        User sharedWith = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (owner.getId().equals(sharedWith.getId())) {
            return ResponseEntity.status(400).body(Map.of("message", "Cannot share with yourself"));
        }
        
        Optional<SharedChecklist> existing = sharedChecklistRepository
                .findByOwnerIdAndSharedWithId(owner.getId(), sharedWith.getId());
        
        if (existing.isPresent()) {
            return ResponseEntity.status(400).body(Map.of("message", "Already shared with this user"));
        }
        
        SharedChecklist shared = new SharedChecklist(owner, sharedWith, request.isCanEdit());
        sharedChecklistRepository.save(shared);
        
        activityLogService.log(owner, "SHARE", "CHECKLIST", sharedWith.getId(), 
            "Shared checklist with " + sharedWith.getUsername(), httpRequest);
        
        return ResponseEntity.ok(Map.of(
            "message", "Checklist shared successfully",
            "sharedWith", sharedWith.getUsername()
        ));
    }

    @GetMapping("/shared-with-me")
    public ResponseEntity<?> getSharedWithMe(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        
        List<SharedChecklist> sharedLists = sharedChecklistRepository.findBySharedWithId(user.getId());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SharedChecklist shared : sharedLists) {
            Map<String, Object> sharedData = new HashMap<>();
            sharedData.put("id", shared.getId());
            sharedData.put("owner", shared.getOwner().getUsername());
            sharedData.put("ownerId", shared.getOwner().getId());
            sharedData.put("canEdit", shared.isCanEdit());
            sharedData.put("sharedDate", shared.getSharedDate().toString());
            
            // Get the owner's checklist items
            List<ChecklistItemStatus> items = checklistStatusRepository.findByUserId(shared.getOwner().getId());
            sharedData.put("itemCount", items.size());
            sharedData.put("completedCount", items.stream().filter(ChecklistItemStatus::isChecked).count());
            
            result.add(sharedData);
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/shared-by-me")
    public ResponseEntity<?> getSharedByMe(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        
        List<SharedChecklist> sharedLists = sharedChecklistRepository.findByOwnerId(user.getId());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (SharedChecklist shared : sharedLists) {
            Map<String, Object> sharedData = new HashMap<>();
            sharedData.put("id", shared.getId());
            sharedData.put("sharedWith", shared.getSharedWith().getUsername());
            sharedData.put("sharedWithId", shared.getSharedWith().getId());
            sharedData.put("canEdit", shared.isCanEdit());
            sharedData.put("sharedDate", shared.getSharedDate().toString());
            result.add(sharedData);
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserChecklist(@PathVariable Long userId,
                                              @RequestHeader("Authorization") String authHeader) {
        User currentUser = getUserFromToken(authHeader);
        
        // Check if current user is the owner or has access
        if (!currentUser.getId().equals(userId)) {
            SharedChecklist shared = sharedChecklistRepository
                    .findByOwnerIdAndSharedWithId(userId, currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Access denied"));
        }
        
        List<ChecklistItemStatus> items = checklistStatusRepository.findByUserId(userId);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChecklistItemStatus item : items) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("id", item.getId());
            itemData.put("itemId", item.getChecklistItemId());
            itemData.put("completed", item.isChecked());
            result.add(itemData);
        }
        
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/share/{id}")
    public ResponseEntity<?> unshareChecklist(@PathVariable Long id,
                                              @RequestHeader("Authorization") String authHeader,
                                              HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        SharedChecklist shared = sharedChecklistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shared checklist not found"));
        
        if (!shared.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
        }
        
        sharedChecklistRepository.delete(shared);
        
        activityLogService.log(user, "UNSHARE", "CHECKLIST", shared.getSharedWith().getId(), 
            "Unshared checklist with " + shared.getSharedWith().getUsername(), httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "Checklist unshared successfully"));
    }

    @PutMapping("/share/{id}")
    public ResponseEntity<?> updateSharePermissions(@PathVariable Long id,
                                                    @RequestHeader("Authorization") String authHeader,
                                                    @RequestBody ShareRequest request,
                                                    HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        SharedChecklist shared = sharedChecklistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shared checklist not found"));
        
        if (!shared.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
        }
        
        shared.setCanEdit(request.isCanEdit());
        sharedChecklistRepository.save(shared);
        
        activityLogService.log(user, "UPDATE", "SHARED_CHECKLIST", id, 
            "Updated share permissions", httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "Permissions updated successfully"));
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    static class ShareRequest {
        private String username;
        private boolean canEdit;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public boolean isCanEdit() { return canEdit; }
        public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }
    }
}
