package com.brothers.shooter_game.models.game;

public class Bullet {
    private double x;
    private double y;
    private double rotation;
    private double speed;

    public Bullet(double x, double y, double rotation, double speed) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.speed = speed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRotation() {
        return rotation;
    }

    public double getSpeed() {
        return speed;
    }
}
