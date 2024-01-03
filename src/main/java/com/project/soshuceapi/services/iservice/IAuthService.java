package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.requests.LoginRequest;

import java.util.Map;

public interface IAuthService {

    Map<String, Object> authenticate(LoginRequest loginRequest);

}
