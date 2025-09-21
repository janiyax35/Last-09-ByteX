package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.ActivityLog;
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
    public ActivityLog saveLog(ActivityLog log) {
        return activityLogRepository.save(log);
    }
}
