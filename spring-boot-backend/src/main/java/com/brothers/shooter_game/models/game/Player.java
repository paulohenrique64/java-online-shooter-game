package com.brothers.shooter_game.models.game;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerSessionId;
    private String username;
    private int life;
    private double x;
    private double y;
    private int speedPlayer;
    private List<Point> positionWall;
    private Weapon weapon;

    public Player(String playerSessionId, String username, List<Point> positionWall) {
        this.playerSessionId = playerSessionId;
        this.username = username;
        this.life = 100;
        this.positionWall = positionWall;
        this.speedPlayer = 5;
        this.x = 200;
        this.y = 200;
        this.weapon = new Weapon();
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

    public void fire(List<Player> playerList) {
        Bullet bullet = new Bullet(this.x, this.y, this.weapon.getAngle(), 20);


    }

    public void respawn() {
        this.life = 100;
    }

    public void damage() {
        this.life -= 5;
    }

    public boolean alive() {
        return this.life > 0;
    }

    public void moveDown() {
        this.y += this.speedPlayer;
        this.checkPosition();
    }

    public void moveUp() {
        this.y -= this.speedPlayer;
        this.checkPosition();
    }

    public void moveRight() {
        this.x += this.speedPlayer;
        this.checkPosition();
    }

    public void moveLeft() {
        this.x -= this.speedPlayer;
        this.checkPosition();
    }

    public void checkPosition() {
        double antX = this.x;
        double antY = this.y;
        double playerSize = 30;
        double tileSize = 110;

         for (int i = 0; i < this.positionWall.size(); i++) {
             Point wall = this.positionWall.get(i);

             double playerLeft = this.x - playerSize / 2;
             double playerRight = this.x + playerSize / 2;
             double playerTop = this.y - playerSize / 2;
             double playerBottom = this.y + playerSize / 2;

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
                         this.x = wallLeft - playerSize / 2;
                     } else if (playerLeft < wallRight && antX > wall.x) {
                         this.x = wallRight + playerSize / 2;
                     }
                 } else {
                     if (playerBottom > wallTop && antY < wall.y) {
                         this.y = wallTop - playerSize / 2;
                     } else if (playerTop < wallBottom && antY > wall.y) {
                         this.y = wallBottom + playerSize / 2;
                     }
                 }
             }
         }
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
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
}
