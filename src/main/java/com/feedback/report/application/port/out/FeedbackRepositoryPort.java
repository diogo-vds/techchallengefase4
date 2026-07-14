package com.feedback.report.application.port.out;


import com.feedback.report.domain.model.Feedback;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackRepositoryPort {
    List<Feedback> findFeedbacksByDateRange(LocalDateTime start, LocalDateTime end);
}
