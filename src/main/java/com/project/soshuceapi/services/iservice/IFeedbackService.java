package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.Feedback;
import com.project.soshuceapi.models.requests.FeedbackCreateRequest;
import com.project.soshuceapi.models.requests.FeedbackSearchRequest;

import java.util.List;

public interface IFeedbackService {

    List<Feedback> getAll(FeedbackSearchRequest request);

    void create(FeedbackCreateRequest request);

}
