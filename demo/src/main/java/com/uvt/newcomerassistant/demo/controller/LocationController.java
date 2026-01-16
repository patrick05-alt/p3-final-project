// LAB 12 - REQUIREMENT 6: Database connection and query data (locations, favorites)
// LAB 12 - REQUIREMENT 7: Insert/update data into DB (create/update locations, favorites)
// LAB 12 - REQUIREMENT 9: Input validation
// LAB 12 - REQUIREMENT 11: REST endpoints for location management
package com.uvt.newcomerassistant.demo.controller;

import com.uvt.newcomerassistant.demo.*;
import com.uvt.newcomerassistant.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private FavoriteLocationRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<?> getAllLocations(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minRating) {
        
        List<Location> locations = locationRepository.findAll();
        
        // Apply filters
        if (category != null && !category.isEmpty()) {
            locations = locations.stream()
                .filter(l -> l.getCategory() != null && l.getCategory().equalsIgnoreCase(category))
                .toList();
        }
        
        if (name != null && !name.isEmpty()) {
            locations = locations.stream()
                .filter(l -> l.getName() != null && l.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        }
        
        if (minRating != null) {
            locations = locations.stream()
                .filter(l -> l.getRating() != null && l.getRating() >= minRating)
                .toList();
        }
        
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocationById(@PathVariable Long id,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        Map<String, Object> response = buildLocationResponse(location);
        
        // Check if favorite for current user
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            User user = getUserFromToken(authHeader);
            boolean isFavorite = favoriteRepository.findByLocationIdAndUserId(id, user.getId()).isPresent();
            response.put("isFavorite", isFavorite);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> addToFavorites(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authHeader,
                                           @RequestBody(required = false) FavoriteRequest request,
                                           HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        Optional<FavoriteLocation> existing = favoriteRepository.findByLocationIdAndUserId(id, user.getId());
        
        if (existing.isPresent()) {
            return ResponseEntity.status(400).body(Map.of("message", "Already in favorites"));
        }
        
        FavoriteLocation favorite = new FavoriteLocation(location, user);
        if (request != null && request.getNotes() != null) {
            favorite.setNotes(request.getNotes());
        }
        
        favoriteRepository.save(favorite);
        
        activityLogService.log(user, "FAVORITE", "LOCATION", id, "Added to favorites", httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "Added to favorites successfully"));
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long id,
                                                 @RequestHeader("Authorization") String authHeader,
                                                 HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        FavoriteLocation favorite = favoriteRepository.findByLocationIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Not in favorites"));
        
        favoriteRepository.delete(favorite);
        
        activityLogService.log(user, "UNFAVORITE", "LOCATION", id, "Removed from favorites", httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "Removed from favorites successfully"));
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getMyFavorites(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        
        List<FavoriteLocation> favorites = favoriteRepository.findByUserId(user.getId());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (FavoriteLocation favorite : favorites) {
            Map<String, Object> favoriteData = new HashMap<>();
            favoriteData.put("id", favorite.getId());
            favoriteData.put("notes", favorite.getNotes());
            favoriteData.put("addedDate", favorite.getAddedDate().toString());
            favoriteData.put("location", buildLocationResponse(favorite.getLocation()));
            result.add(favoriteData);
        }
        
        return ResponseEntity.ok(result);
    }

    @PutMapping("/favorites/{id}")
    public ResponseEntity<?> updateFavoriteNotes(@PathVariable Long id,
                                                 @RequestHeader("Authorization") String authHeader,
                                                 @RequestBody FavoriteRequest request,
                                                 HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        FavoriteLocation favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        
        if (!favorite.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "Unauthorized"));
        }
        
        favorite.setNotes(request.getNotes());
        favoriteRepository.save(favorite);
        
        activityLogService.log(user, "UPDATE", "FAVORITE_LOCATION", id, "Updated favorite notes", httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "Notes updated successfully"));
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadLocationImage(@PathVariable Long id,
                                                 @RequestParam("file") MultipartFile file,
                                                 @RequestHeader("Authorization") String authHeader,
                                                 HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        try {
            String uploadDir = "uploads/locations/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String fileName = id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            
            location.setImageUrl("/uploads/locations/" + fileName);
            locationRepository.save(location);
            
            activityLogService.log(user, "UPDATE", "LOCATION", id, "Location image uploaded", httpRequest);
            
            return ResponseEntity.ok(Map.of(
                "message", "Image uploaded successfully",
                "imageUrl", location.getImageUrl()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to upload image"));
        }
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Map<String, Object> buildLocationResponse(Location location) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", location.getId());
        response.put("name", location.getName());
        response.put("category", location.getCategory());
        response.put("details", location.getDetails());
        response.put("address", location.getAddress());
        response.put("phone", location.getPhone());
        response.put("website", location.getWebsite());
        response.put("imageUrl", location.getImageUrl());
        response.put("rating", location.getRating());
        response.put("hours", location.getHours());
        return response;
    }

    static class FavoriteRequest {
        private String notes;

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
