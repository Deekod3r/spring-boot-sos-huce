package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.entities.FeedbackBox;
import com.project.soshuceapi.models.requests.FeedbackCreateRequest;
import com.project.soshuceapi.models.requests.FeedbackSearchRequest;

import java.util.List;

public interface IFeedbackBoxService {

    List<FeedbackBox> getAll(FeedbackSearchRequest request);

    void create(FeedbackCreateRequest request);

}
