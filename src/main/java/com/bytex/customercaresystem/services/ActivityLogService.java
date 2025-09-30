package com.bytex.customercaresystem.services;

import com.bytex.customercaresystem.models.ActivityLog;
import com.bytex.customercaresystem.models.User;
import com.bytex.customercaresystem.repositories.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logActivity(User user, String actionType, String entityType, Long entityId, String description) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setCreatedAt(LocalDateTime.now());
        // In a real web context, you could get the IP from the request
        // log.setIpAddress(request.getRemoteAddr());
        activityLogRepository.save(log);
    }

    public List<ActivityLog> findAllLogs() {
        return activityLogRepository.findAll();
    }
}