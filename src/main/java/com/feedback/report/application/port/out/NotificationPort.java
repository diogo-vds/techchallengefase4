package com.feedback.report.application.port.out;


import com.feedback.report.domain.model.WeeklyReport;

public interface NotificationPort {
    void sendWeeklyReport(WeeklyReport report);
}
