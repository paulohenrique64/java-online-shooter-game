package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.models.auth.User;
import com.brothers.shooter_game.models.game.*;
import com.brothers.shooter_game.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.awt.geom.Point2D;
import java.util.List;

@RestController
@RequestMapping("game")
public class GameController implements ApplicationListener {
    @Autowired
    Room gameRoom;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    UserRepository userRepository;

    @MessageMapping("/start-game")
    @SendTo("/log/start-game")
    private RoomDTO startGame(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken, @Header("simpSessionId") String sessionId) {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        Player player = new Player(sessionId, userdata.getName(), gameRoom.getGameMap().getPositionWall(), gameRoom.getGameMap().getRespawnArea());

        if (gameRoom.addPlayer(player))
            System.out.println("new player in the room " + gameRoom.getId() + " with name " + player.getUsername() + " and sessionId " + sessionId);

        return new RoomDTO(this.gameRoom);
    }

    @MessageMapping("/game-data")
    @SendTo("/log/game-data")
    public RoomDTO gameData(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        return new RoomDTO(this.gameRoom);
    }

    @MessageMapping("/player-position")
    @SendTo("/log/game-data")
    public RoomDTO updatePlayerPosition(@Payload String message, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message);

        gameRoom.movePlayer(userdata.getName(), jsonNode.get("key").toString());
        return new RoomDTO(this.gameRoom);
    }

    @MessageMapping("/fire")
    @SendTo("/log/fire")
    public BulletDTO fire(@Payload String message, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message);

        double x = jsonNode.get("x").decimalValue().doubleValue();
        double y = jsonNode.get("y").decimalValue().doubleValue();
        double angle = jsonNode.get("angle").decimalValue().doubleValue();

        Bullet bullet = new Bullet(new Point2D.Double(x, y), angle,  userdata.getName());

        if (gameRoom.playerFire(bullet))
            this.sendGameData();

        return new BulletDTO(new Bullet(new Point2D.Double(x, y), angle, userdata.getName()));
    }

    @MessageMapping("/respawn")
    @SendTo("/log/game-data")
    public RoomDTO respawnPlayer(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        gameRoom.respawn(userdata.getName());
        return new RoomDTO(this.gameRoom);
    }

    @MessageMapping("/weapon-movement")
    @SendTo("/log/game-data")
    public RoomDTO weaponMovement(@Payload String message, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws Exception {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message);

        double angle = jsonNode.get("angle").decimalValue().doubleValue();

        this.gameRoom.setPlayerWeaponAngle(angle, userdata.getName());
        return new RoomDTO(this.gameRoom);
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        SessionDisconnectEvent sessionDisconnectEvent = (SessionDisconnectEvent) event;
        String sessionIdDisconnected = sessionDisconnectEvent.getSessionId();

        List<Player> playerList = gameRoom.getPlayerList();

        for (Player player : playerList) {
            if (player.getPlayerSessionId().equals(sessionIdDisconnected)) {
                User user = (User) userRepository.findByName(player.getUsername());
                user.setKills(user.getKills() + player.getKills());
                user.setScore(user.getScore() + (player.getKills() * Math.random() * 10));
                userRepository.deleteById(user.getId());
                userRepository.save(user);
            }
        }

        if (gameRoom.removePlayerWithSessionId(sessionIdDisconnected))
            System.out.println("player with id '" + sessionDisconnectEvent.getSessionId() + "' DISCONNECTED of game lobby");

        this.sendGameData();
    }

    public void sendGameData() {
        simpMessagingTemplate.convertAndSend("/log/game-data", new RoomDTO(this.gameRoom));
    }
}
