package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.ActivityLog;
import com.bytex.customercaresystem.model.User;
import java.util.List;

public interface ActivityLogService {
    void saveLog(User user, String actionType, String description);
    List<ActivityLog> findAllLogs();
}
