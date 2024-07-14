package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.Models.OnlinePlayersListDTO;
import com.brothers.shooter_game.Models.Session;
import com.brothers.shooter_game.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;

@Controller
public class LobbyController implements ApplicationListener  {
    ArrayList<Session> sessions = new ArrayList<Session>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/online-players-list")
    @SendTo("/log/online-players-list")
    public OnlinePlayersListDTO updateOnlinePlayersList(Message message, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken, @Header("simpSessionId") String sessionId) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        System.out.println("user '" + userdata.getName() + "' with id '" + sessionId + "' CONNECTED in game lobby");

        boolean has = false;
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getAttribute("username") == userdata.getName())
                has = true;
        }

        if (!has)
            sessions.add(new Session(sessionId, userdata.getName()));

        return new OnlinePlayersListDTO(this.sessions);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        SessionDisconnectEvent sessionDisconnectEvent = (SessionDisconnectEvent) event;
        String username = null;

        for (int i = 0; i < sessions.size(); i++) {
            if (this.sessions.get(i).getId().equals(sessionDisconnectEvent.getSessionId())) {
                username = this.sessions.get(i).getUsername();
                this.sessions.remove(i);
                break;
            }
        }

        System.out.println("user '" + username + "' with id '" + sessionDisconnectEvent.getSessionId() + "' DISCONNECTED of game lobby");
    }
}


