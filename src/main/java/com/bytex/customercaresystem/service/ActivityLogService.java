package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.ActivityLog;
import java.util.List;

public interface ActivityLogService {

    /**
     * Retrieves all activity logs from the database.
     * @return A list of all ActivityLog objects.
     */
    List<ActivityLog> findAllLogs();

    /**
     * Saves a new activity log with detailed information.
     * @param user The user performing the action.
     * @param actionType The type of action (e.g., CREATE, UPDATE, LOGIN).
     * @param description A human-readable description of the action.
     */
    void saveLog(User user, String actionType, String description);

}
