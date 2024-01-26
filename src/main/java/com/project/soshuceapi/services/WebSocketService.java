package com.project.soshuceapi.services;

import com.project.soshuceapi.services.iservice.IWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService implements IWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendLogoutMessage(String email) {
        messagingTemplate.convertAndSend("/topic/logout", email);
    }

}
