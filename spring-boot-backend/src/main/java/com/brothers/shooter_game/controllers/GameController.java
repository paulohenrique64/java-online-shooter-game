package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.models.auth.Session;
import com.brothers.shooter_game.models.auth.User;
import com.brothers.shooter_game.models.game.OnlinePlayersListDTO;
import com.brothers.shooter_game.models.game.Player;
import com.brothers.shooter_game.models.game.Room;
import com.brothers.shooter_game.models.game.RoomDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
@RequestMapping("game")
public class GameController implements ApplicationListener {
    @Autowired
    Room gameRoom;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/start-game")
    @SendTo("/log/start-game")
    private RoomDTO startGame(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken, @Header("simpSessionId") String sessionId) {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        Player player = new Player(sessionId, userdata.getName(), gameRoom.getGameMap().getPositionWall());

        if (gameRoom.addPlayer(player))
            System.out.println("new player in the room " + gameRoom.getId() + " with name " + player.getUsername() + " and sessionId " + sessionId);

        return new RoomDTO(this.gameRoom);
    }

    @MessageMapping("/game-data")
    @SendTo("/log/game-data")
    public RoomDTO gameData(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws Exception {
        return new RoomDTO(this.gameRoom);
    }

    @MessageMapping("/player-position")
    @SendTo("/log/game-data")
    public RoomDTO updatePlayerPosition(@Payload String message, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        System.out.println("player: " + userdata.getName() + " se moveu para a: " + message.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message);

        gameRoom.movePlayer(userdata.getName(), jsonNode.get("key").toString());
        System.out.println(jsonNode.get("key"));

        return new RoomDTO(this.gameRoom);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        SessionDisconnectEvent sessionDisconnectEvent = (SessionDisconnectEvent) event;
        String sessionIdDisconnected = sessionDisconnectEvent.getSessionId();

        if (gameRoom.removePlayerWithSessionId(sessionIdDisconnected))
            System.out.println("player with id '" + sessionDisconnectEvent.getSessionId() + "' DISCONNECTED of game lobby");

        System.out.println(gameRoom.getPlayerList().toString());

        this.sendGameData();
    }

    public void sendGameData() {
        simpMessagingTemplate.convertAndSend("/log/game-data", new RoomDTO(this.gameRoom));
    }
}
