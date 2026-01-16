// LAB 12 - REQUIREMENT 6: Database connection and query data (events, RSVPs)
// LAB 12 - REQUIREMENT 7: Insert/update data into DB (create/update events, RSVPs)
// LAB 12 - REQUIREMENT 9: Input validation
// LAB 12 - REQUIREMENT 11: REST endpoints for event management
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
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private EventRSVPRepository rsvpRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        List<Event> events = eventRepository.findAll();
        
        // Apply filters
        if (category != null && !category.isEmpty()) {
            events = events.stream()
                .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(category))
                .toList();
        }
        
        if (location != null && !location.isEmpty()) {
            events = events.stream()
                .filter(e -> e.getLocation() != null && e.getLocation().toLowerCase().contains(location.toLowerCase()))
                .toList();
        }
        
        if (dateFrom != null && !dateFrom.isEmpty()) {
            LocalDate from = LocalDate.parse(dateFrom);
            events = events.stream()
                .filter(e -> !e.getDate().isBefore(from))
                .toList();
        }
        
        if (dateTo != null && !dateTo.isEmpty()) {
            LocalDate to = LocalDate.parse(dateTo);
            events = events.stream()
                .filter(e -> !e.getDate().isAfter(to))
                .toList();
        }
        
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        Map<String, Object> response = buildEventResponse(event);
        
        // Add RSVP count
        List<EventRSVP> rsvps = rsvpRepository.findByEventId(id);
        long attendingCount = rsvps.stream().filter(r -> "ATTENDING".equals(r.getStatus())).count();
        response.put("rsvpCount", attendingCount);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/rsvp")
    public ResponseEntity<?> rsvpToEvent(@PathVariable Long id,
                                         @RequestHeader("Authorization") String authHeader,
                                         @RequestBody RSVPRequest request,
                                         HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        Optional<EventRSVP> existingRSVP = rsvpRepository.findByEventIdAndUserId(id, user.getId());
        
        EventRSVP rsvp;
        if (existingRSVP.isPresent()) {
            rsvp = existingRSVP.get();
            rsvp.setStatus(request.getStatus());
        } else {
            rsvp = new EventRSVP(event, user, request.getStatus());
        }
        
        rsvp = rsvpRepository.save(rsvp);
        
        activityLogService.log(user, "RSVP", "EVENT", id, "RSVP status: " + request.getStatus(), httpRequest);
        
        return ResponseEntity.ok(Map.of(
            "message", "RSVP updated successfully",
            "status", rsvp.getStatus()
        ));
    }

    @GetMapping("/{id}/rsvp")
    public ResponseEntity<?> getUserRSVP(@PathVariable Long id,
                                         @RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        
        Optional<EventRSVP> rsvp = rsvpRepository.findByEventIdAndUserId(id, user.getId());
        
        if (rsvp.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "status", rsvp.get().getStatus(),
                "rsvpDate", rsvp.get().getRsvpDate().toString()
            ));
        }
        
        return ResponseEntity.ok(Map.of("status", "NONE"));
    }

    @GetMapping("/my-rsvps")
    public ResponseEntity<?> getMyRSVPs(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        
        List<EventRSVP> rsvps = rsvpRepository.findByUserId(user.getId());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (EventRSVP rsvp : rsvps) {
            Map<String, Object> rsvpData = new HashMap<>();
            rsvpData.put("id", rsvp.getId());
            rsvpData.put("status", rsvp.getStatus());
            rsvpData.put("rsvpDate", rsvp.getRsvpDate().toString());
            rsvpData.put("event", buildEventResponse(rsvp.getEvent()));
            result.add(rsvpData);
        }
        
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}/rsvp")
    public ResponseEntity<?> cancelRSVP(@PathVariable Long id,
                                        @RequestHeader("Authorization") String authHeader,
                                        HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        
        EventRSVP rsvp = rsvpRepository.findByEventIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("RSVP not found"));
        
        rsvpRepository.delete(rsvp);
        
        activityLogService.log(user, "CANCEL_RSVP", "EVENT", id, "RSVP cancelled", httpRequest);
        
        return ResponseEntity.ok(Map.of("message", "RSVP cancelled successfully"));
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadEventImage(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file,
                                              @RequestHeader("Authorization") String authHeader,
                                              HttpServletRequest httpRequest) {
        User user = getUserFromToken(authHeader);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        try {
            String uploadDir = "uploads/events/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String fileName = id + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());
            
            event.setImageUrl("/uploads/events/" + fileName);
            eventRepository.save(event);
            
            activityLogService.log(user, "UPDATE", "EVENT", id, "Event image uploaded", httpRequest);
            
            return ResponseEntity.ok(Map.of(
                "message", "Image uploaded successfully",
                "imageUrl", event.getImageUrl()
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

    private Map<String, Object> buildEventResponse(Event event) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", event.getId());
        response.put("name", event.getName());
        response.put("date", event.getDate().toString());
        response.put("location", event.getLocation());
        response.put("description", event.getDescription());
        response.put("category", event.getCategory());
        response.put("imageUrl", event.getImageUrl());
        response.put("capacity", event.getCapacity());
        response.put("organizer", event.getOrganizer());
        return response;
    }

    static class RSVPRequest {
        private String status; // ATTENDING, MAYBE, NOT_ATTENDING

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
