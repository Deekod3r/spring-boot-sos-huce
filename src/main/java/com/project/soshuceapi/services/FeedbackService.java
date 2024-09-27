package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.Feedback;
import com.project.soshuceapi.models.requests.FeedbackCreateRequest;
import com.project.soshuceapi.models.requests.FeedbackSearchRequest;
import com.project.soshuceapi.repositories.FeedbackRepo;
import com.project.soshuceapi.services.iservice.IFeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FeedbackService implements IFeedbackService {

    private static final String TAG = "FEEDBACK";

    @Autowired
    private FeedbackRepo feedbackRepo;

    @Override
    public List<Feedback> getAll(FeedbackSearchRequest request) {
        try {
            return feedbackRepo.findAll(request.getFromDate(), request.getToDate());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(FeedbackCreateRequest request) {
        try {
            Feedback feedback = new Feedback();
            feedback.setFullName(request.getFullName().trim());
            feedback.setMessage(request.getMessage().trim());
            feedback.setEmail(request.getEmail().trim());
            feedback.setIsRead(false);
            feedback.setCreatedAt(LocalDateTime.now());
            feedbackRepo.save(feedback);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
