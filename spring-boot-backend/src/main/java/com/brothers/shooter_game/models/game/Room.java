package com.brothers.shooter_game.models.game;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.List;

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

    public boolean playerFire(Bullet bullet) {
        boolean mapCollision = false;
        boolean playerCollision = false;

        do {
            mapCollision = checkBulletMapCollision(bullet);
            playerCollision = checkBulletPlayerCollision(bullet);

            if (!mapCollision && !playerCollision) {
                // update shooter position
                bullet.getPosition().x += Math.cos(bullet.getAngle()) * bullet.getSpeed();
                bullet.getPosition().y += Math.sin(bullet.getAngle()) * bullet.getSpeed();
            }

            if (playerCollision) return true;
        } while (!mapCollision && !playerCollision);

        return false;
    }

    public boolean checkBulletMapCollision(Bullet bullet) {
        int bulletSize = 9;
        int tileSize = 110;
        List<Point> positionWall = gameMap.getPositionWall();

        double bulletLeft = bullet.getPosition().x - bulletSize / (double) 2;
        double bulletRight = bullet.getPosition().x + bulletSize / (double) 2;
        double bulletTop = bullet.getPosition().y - bulletSize / (double) 2;
        double bulletBottom = bullet.getPosition().y + bulletSize / (double) 2;

        for (int i = 0; i < positionWall.size(); i++) {
            Point wall = positionWall.get(i);

            double wallLeft = wall.x;
            double wallRight = wall.x + tileSize;
            double wallTop = wall.y;
            double wallBottom = wall.y + tileSize;

            if (bulletRight > wallLeft && bulletLeft < wallRight && bulletBottom > wallTop && bulletTop < wallBottom) {
                return true;
            }
        }

        return false;
    }

    public boolean checkBulletPlayerCollision(Bullet bullet) {
        int bulletSize = 9;
        int playerSize = 35;

        double bulletLeft = bullet.getPosition().x - bulletSize / (double) 2;
        double bulletRight = bullet.getPosition().x + bulletSize / (double) 2;
        double bulletTop = bullet.getPosition().y - bulletSize / (double) 2;
        double bulletBottom = bullet.getPosition().y + bulletSize / (double) 2;

        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);

            double playerLeft = player.getPosition().x - playerSize / (double)2;
            double playerRight = player.getPosition().x + playerSize / (double) 2;
            double playerTop = player.getPosition().y - playerSize / (double) 2;
            double playerBottom = player.getPosition().y + playerSize / (double) 2;

            if (bulletRight > playerLeft && bulletLeft < playerRight && bulletBottom > playerTop && bulletTop < playerBottom) {
                player.damage();
                
                if (!player.alive())
                    for (Player p : this.playerList)
                        if (p.getUsername().equals(bullet.getShooterName()))
                            p.setKills(p.getKills() + 1);

                return true;
            }
        }

        return false;
    }

    public void respawn(String username) {
        for (Player player : this.playerList) {
            if (player.getUsername().equals(username)) {
                player.respawn();
            }
        }
    }

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
