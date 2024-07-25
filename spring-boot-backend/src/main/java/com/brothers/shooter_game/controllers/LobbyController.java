package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.models.game.OnlinePlayersListDTO;
import com.brothers.shooter_game.models.auth.Session;
import com.brothers.shooter_game.models.auth.User;
import com.brothers.shooter_game.repository.SessionRepository;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Optional;

@CrossOrigin("http://127.0.0.1:3000")
@Controller
public class LobbyController implements ApplicationListener  {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    SessionRepository sessionRepo;

    @MessageMapping("/online-players-list")
    @SendTo("/log/online-players-list")
    public OnlinePlayersListDTO updateOnlinePlayersList(Message message, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken, @Header("simpSessionId") String sessionId) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();

        boolean has = false;
        List<Session> sessions = sessionRepo.findAll();

        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getUsername().equals(userdata.getName()))
                has = true;
        }

        if (!has) {
            System.out.println("user '" + userdata.getName() + "' with id '" + sessionId + "' CONNECTED in game lobby");
            sessionRepo.save(new Session(sessionId, userdata.getName()));
        }

        return new OnlinePlayersListDTO(sessionRepo.findAll());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        SessionDisconnectEvent sessionDisconnectEvent = (SessionDisconnectEvent) event;
        Optional opt = sessionRepo.findById(sessionDisconnectEvent.getSessionId());
        Session session = null;

        if (opt.isPresent())
            session = (Session) opt.get();

        if (session != null) {
            sessionRepo.deleteByUsername(session.getUsername());
            System.out.println("user '" + session.getUsername() + "' with id '" + sessionDisconnectEvent.getSessionId() + "' DISCONNECTED of game lobby");
        }

        sendOnlinePlayersList();
    }

    public void sendOnlinePlayersList() {
        simpMessagingTemplate.convertAndSend("/log/online-players-list", new OnlinePlayersListDTO(sessionRepo.findAll()));
    }
}



