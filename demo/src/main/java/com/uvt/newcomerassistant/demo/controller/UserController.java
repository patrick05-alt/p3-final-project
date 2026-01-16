// LAB 12 - REQUIREMENT 6: Database connection and query data (profile, activity logs)
// LAB 12 - REQUIREMENT 7: Insert/update data into DB (update profile, change password)
// LAB 12 - REQUIREMENT 9: Input validation
// LAB 12 - REQUIREMENT 11: REST endpoints for user management
package com.uvt.newcomerassistant.demo.controller;

import com.uvt.newcomerassistant.demo.*;
import com.uvt.newcomerassistant.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        return ResponseEntity.ok(buildUserResponse(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody ProfileUpdateRequest request,
                                          HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getPreferredLanguage() != null) user.setPreferredLanguage(request.getPreferredLanguage());
        if (request.getTheme() != null) user.setTheme(request.getTheme());
        if (request.getEmailNotifications() != null) user.setEmailNotifications(request.getEmailNotifications());
        
        user = userRepository.save(user);
        
        activityLogService.log(user, "UPDATE", "USER", user.getId(), "Profile updated", httpRequest);
        
        return ResponseEntity.ok(buildUserResponse(user));
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestHeader("Authorization") String authHeader,
                                                   @RequestParam("file") MultipartFile file,
                                                   HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        try {
            // Save file to uploads directory
            String uploadDir = "uploads/profiles/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String fileName = user.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            
            user.setProfilePicture("/uploads/profiles/" + fileName);
            userRepository.save(user);
            
            activityLogService.log(user, "UPDATE", "USER", user.getId(), "Profile picture updated", httpRequest);
            
            return ResponseEntity.ok(Map.of(
                "message", "Profile picture uploaded successfully",
                "imageUrl", user.getProfilePicture()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to upload image"));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody ChangePasswordRequest request,
                                           HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("message", "Current password is incorrect"));
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        activityLogService.log(user, "PASSWORD_CHANGE", "USER", user.getId(), "Password changed", httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @GetMapping("/activity-logs")
    public ResponseEntity<?> getActivityLogs(@RequestHeader("Authorization") String authHeader,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        User user = getUserFromToken(authHeader);
        Page<ActivityLog> logs = activityLogRepository.findByUserIdOrderByTimestampDesc(
            user.getId(), PageRequest.of(page, size)
        );
        
        List<Map<String, Object>> logData = new ArrayList<>();
        for (ActivityLog log : logs) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("id", log.getId());
            logMap.put("action", log.getAction());
            logMap.put("entity", log.getEntity());
            logMap.put("entityId", log.getEntityId());
            logMap.put("details", log.getDetails());
            logMap.put("timestamp", log.getTimestamp().toString());
            logData.add(logMap);
        }
        
        return ResponseEntity.ok(Map.of(
            "logs", logData,
            "totalPages", logs.getTotalPages(),
            "currentPage", page
        ));
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("phone", user.getPhone());
        response.put("country", user.getCountry());
        response.put("city", user.getCity());
        response.put("bio", user.getBio());
        response.put("profilePicture", user.getProfilePicture());
        response.put("preferredLanguage", user.getPreferredLanguage());
        response.put("theme", user.getTheme());
        response.put("emailNotifications", user.isEmailNotifications());
        return response;
    }

    static class ProfileUpdateRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String country;
        private String city;
        private String bio;
        private String preferredLanguage;
        private String theme;
        private Boolean emailNotifications;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getPreferredLanguage() { return preferredLanguage; }
        public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
        public String getTheme() { return theme; }
        public void setTheme(String theme) { this.theme = theme; }
        public Boolean getEmailNotifications() { return emailNotifications; }
        public void setEmailNotifications(Boolean emailNotifications) { this.emailNotifications = emailNotifications; }
    }

    static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
