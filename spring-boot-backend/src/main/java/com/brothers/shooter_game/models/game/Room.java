package com.brothers.shooter_game.models.game;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Room {
    private String id;
    private final List<Player> playerList = new ArrayList<Player>();
    private final GameMap gameMap = new GameMap();

    public Room() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void changeId(String id) {
        this.id = UUID.randomUUID().toString();
    }

    public boolean addPlayer(Player player) {
        boolean has = false;

        for (int i = 0; i < this.playerList.size(); i++) {
            if (this.playerList.get(i).getUsername().equals(player.getUsername()))
                has = true;
        }

        if (!has)
            this.playerList.add(player);

        return !has;
    }

    public boolean removePlayer(Player player) {
        for (int i = 0; i < this.playerList.size(); i++) {
            if (this.playerList.get(i).getUsername() == player.getUsername()) {
                this.playerList.remove(i);
                return true;
            }
        }

        return false;
    }

    // THIS METHOD IS VOID FOR WHILE
    // IN THE FUTURE, THIS METHOD WILL RETURN TRUE FOR SUCESS FIRE
    public void playerFire(String username) {
        // simular um tiro saindo de player e batendo no gameMap wall
        this.playerList.stream().forEach(player -> {
            if (player.getUsername().equals(username)) {
                player.fire(this.playerList);
            }
        });
    }

    // THIS METHOD IS VOID FOR WHILE
    // IN FUTURE, THIS METHOD WILL RETURN TRUE FOR SUCESS MOVE ANGLE
    public void setPlayerWeaponAngle(double angle, String username) {
        this.playerList.stream().forEach(player -> {
            if (player.getUsername().equals(username)) {
                player.getWeapon().setAngle(angle);
            }
        });
    }

    public boolean movePlayer(String username, String key) {
        boolean moved = false;
        char realKey = key.charAt(1);

        this.playerList.stream().forEach(player -> {
            if (player.getUsername().equals(username)) {
                switch (realKey) {
                    case 'u':
                        player.moveUp();
                        break;
                    case 'd':
                        player.moveDown();
                        break;
                    case 'l':
                        player.moveLeft();
                        break;
                    case 'r':
                        player.moveRight();
                        break;
                    default:
                        break;
                }
            }
        });

        return true;
    }

    public boolean removePlayerWithSessionId(String sessionId) {
        for (int i = 0; i < this.playerList.size(); i++) {
            if (this.playerList.get(i).getPlayerSessionId().equals(sessionId)) {
                this.playerList.remove(this.playerList.get(i));
                return true;
            }
        }

        return false;
    }

    public int getNumPlayers() {
        return this.playerList.size();
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public GameMap getGameMap() {
        return gameMap;
    }
}
