package com.feedback.report.application.port.in;


import com.feedback.report.domain.model.WeeklyReport;
import java.time.LocalDate;

public interface GenerateReportUseCase {
    WeeklyReport generateWeeklyReport(LocalDate startDate, LocalDate endDate);
}
