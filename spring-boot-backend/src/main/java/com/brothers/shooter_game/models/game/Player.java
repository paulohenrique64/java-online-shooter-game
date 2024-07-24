package com.brothers.shooter_game.models.game;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerSessionId;
    private String username;
    private int life;
    private boolean alive;
    private Point2D.Double position;
    private int speedPlayer;
    private List<Point> positionWall;
    private List<Point> respawnArea;
    private Weapon weapon;
    public int kills;

    public Player(String playerSessionId, String username, List<Point> positionWall, List<Point> respawnArea) {
        this.playerSessionId = playerSessionId;
        this.username = username;
        this.life = 100;
        this.positionWall = positionWall;
        this.respawnArea = respawnArea;
        this.speedPlayer = 5;
        this.weapon = new Weapon();
        this.alive = true;
        this.position = new Point2D.Double();
        this.respawn();
        this.kills = 0;
    }

    public int getLife() {
        return life;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void respawn() {
        this.life = 100;
        this.alive = true;

        do {
            this.position.x = Math.random() * 1000;
            this.position.y = Math.random() * 1000;
        } while (!this.checkPlayerInRespawnArea());
    }

    public boolean checkPlayerInRespawnArea() {
        int playerSize = 35;
        int tileSize = 90;

        double playerLeft = position.x - playerSize / (double) 2;
        double playerRight = position.x + playerSize / (double) 2;
        double playerTop = position.y - playerSize / (double) 2;
        double playerBottom = position.y + playerSize / (double) 2;

        for (int i = 0; i < respawnArea.size(); i++) {
            Point area = respawnArea.get(i);

            double areaLeft = area.x;
            double areaRight = area.x + tileSize;
            double areaTop = area.y;
            double areaBottom = area.y + tileSize;

            if (playerRight > areaLeft && playerLeft < areaRight && playerBottom > areaTop && playerTop < areaBottom) {
                return true;
            }
        }

        return false;
    }

    public void damage() {
        this.life -= 20;

        if (this.life <= 0) {
            this.alive = false;
        }
    }

    public boolean alive() {
        return this.life > 0;
    }

    public void moveDown() {
        this.position.y += this.speedPlayer;
        this.checkPosition();
    }

    public void moveUp() {
        this.position.y -= this.speedPlayer;
        this.checkPosition();
    }

    public void moveRight() {
        this.position.x += this.speedPlayer;
        this.checkPosition();
    }

    public void moveLeft() {
        this.position.x -= this.speedPlayer;
        this.checkPosition();
    }

    public void checkPosition() {
        double antX = this.position.x;
        double antY = this.position.y;
        double playerSize = 30;
        double tileSize = 110;

         for (int i = 0; i < this.positionWall.size(); i++) {
             Point wall = this.positionWall.get(i);

             double playerLeft = this.position.x - playerSize / 2;
             double playerRight = this.position.x + playerSize / 2;
             double playerTop = this.position.y - playerSize / 2;
             double playerBottom = this.position.y + playerSize / 2;

             double wallLeft = wall.x;
             double wallRight = wall.x + tileSize;
             double wallTop = wall.y;
             double wallBottom = wall.y + tileSize;

             if (playerRight > wallLeft && playerLeft < wallRight &&
                 playerBottom > wallTop && playerTop < wallBottom) {

                 double overlapX = Math.min(playerRight - wallLeft, wallRight - playerLeft);
                 double overlapY = Math.min(playerBottom - wallTop, wallBottom - playerTop);

                 if (overlapX < overlapY) {
                     if (playerRight > wallLeft && antX < wall.x) {
                         this.position.x = wallLeft - playerSize / 2;
                     } else if (playerLeft < wallRight && antX > wall.x) {
                         this.position.x = wallRight + playerSize / 2;
                     }
                 } else {
                     if (playerBottom > wallTop && antY < wall.y) {
                         this.position.y = wallTop - playerSize / 2;
                     } else if (playerTop < wallBottom && antY > wall.y) {
                         this.position.y = wallBottom + playerSize / 2;
                     }
                 }
             }
         }
    }

    public void setPlayerSessionId(String playerSessionId) {
        this.playerSessionId = playerSessionId;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public void setSpeedPlayer(int speedPlayer) {
        this.speedPlayer = speedPlayer;
    }

    public List<Point> getPositionWall() {
        return positionWall;
    }

    public void setPositionWall(List<Point> positionWall) {
        this.positionWall = positionWall;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public int getSpeedPlayer() {
        return speedPlayer;
    }

    public String getPlayerSessionId() {
        return playerSessionId;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
