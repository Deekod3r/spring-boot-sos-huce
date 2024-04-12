package com.project.soshuceapi.services;

import com.project.soshuceapi.entities.FeedbackBox;
import com.project.soshuceapi.models.requests.FeedbackCreateRequest;
import com.project.soshuceapi.models.requests.FeedbackSearchRequest;
import com.project.soshuceapi.repositories.FeedbackBoxRepo;
import com.project.soshuceapi.services.iservice.IFeedbackBoxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class FeedbackBoxService implements IFeedbackBoxService {

    private static final String TAG = "FEEDBACK";

    @Autowired
    private FeedbackBoxRepo feedbackBoxRepo;

    @Override
    public List<FeedbackBox> getAll(FeedbackSearchRequest request) {
        try {
            return feedbackBoxRepo.findAll(request.getFromDate(), request.getToDate());
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void create(FeedbackCreateRequest request) {
        try {
            FeedbackBox feedbackBox = new FeedbackBox();
            feedbackBox.setFullName(request.getFullName().trim());
            feedbackBox.setMessage(request.getMessage().trim());
            feedbackBox.setEmail(request.getEmail().trim());
            feedbackBox.setCreatedAt(LocalDateTime.now());
            feedbackBoxRepo.save(feedbackBox);
        } catch (Exception e) {
            log.error(TAG + ": " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
