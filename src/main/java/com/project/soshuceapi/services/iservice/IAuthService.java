package com.project.soshuceapi.services.iservice;

import com.project.soshuceapi.models.requests.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface IAuthService {

    Map<String, Object> authenticate(LoginRequest loginRequest);

    void refreshToken(HttpServletRequest request, HttpServletResponse response);

}
