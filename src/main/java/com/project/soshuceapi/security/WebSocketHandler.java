package com.project.soshuceapi.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public void logoutUser(String userId) {
        WebSocketSession session = sessions.get(userId);
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                log.error("Error while closing session: {}", e.getMessage());
            }
            removeSession(userId);
        }
    }

}


