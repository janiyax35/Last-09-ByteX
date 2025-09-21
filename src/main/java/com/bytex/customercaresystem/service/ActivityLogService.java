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
     * Saves a new activity log.
     * @param log The ActivityLog object to save.
     * @return The saved ActivityLog object.
     */
    ActivityLog saveLog(ActivityLog log);

}
