package com.ss.newsportal.service.feedback;

import com.ss.newsportal.entity.Feedback;

import java.security.Principal;
import java.util.List;


public interface FeedbackService {

    void addFeedback(long newsId, String feedbackText, String authorName, Principal principal);

    List<Feedback> getFeedbacksByNewsId(long newsId);

    void deleteFeedback(long newsId, long feedbackId);
}
