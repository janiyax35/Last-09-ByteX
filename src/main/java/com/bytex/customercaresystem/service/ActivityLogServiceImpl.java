package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.ActivityLog;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.ActivityLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public List<ActivityLog> findAllLogs() {
        // Return logs sorted by creation date, newest first.
        return activityLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public void saveLog(User user, String actionType, String description) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setActionType(actionType);
        log.setDescription(description);
        // In a real app, we would get the IP address from the request context
        log.setIpAddress("127.0.0.1");
        // For simplicity, we are not setting entityType/entityId, but this could be expanded.
        log.setEntityType("GENERAL");
        activityLogRepository.save(log);
    }
}
