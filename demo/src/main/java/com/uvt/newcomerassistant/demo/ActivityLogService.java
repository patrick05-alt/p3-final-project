// LAB 12 - REQUIREMENT 3: Asynchronous logging using Spring @Async for thread-based processing
package com.uvt.newcomerassistant.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ActivityLogService {
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    public void log(User user, String action, String entity, Long entityId, String details) {
        ActivityLog log = new ActivityLog(user, action, entity, entityId, details);
        activityLogRepository.save(log);
    }
    
    public void log(User user, String action, String entity, Long entityId, String details, HttpServletRequest request) {
        ActivityLog log = new ActivityLog(user, action, entity, entityId, details);
        if (request != null) {
            log.setIpAddress(getClientIP(request));
        }
        activityLogRepository.save(log);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
