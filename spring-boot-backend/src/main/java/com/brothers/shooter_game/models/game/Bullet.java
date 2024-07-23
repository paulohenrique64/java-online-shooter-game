package com.brothers.shooter_game.models.game;

public class Bullet {
    private double initialXPosition;
    private double initialYPosition;
    private double angle;
    private double speed;

    public Bullet(double x, double y, double angle, double speed) {
        this.initialXPosition = x;
        this.initialYPosition = y;
        this.angle = angle;
        this.speed = speed;
    }

    public Bullet(double x, double y, double angle) {
        this.initialXPosition = x;
        this.initialYPosition = y;
        this.angle = angle;
        this.speed = 10;
    }

    public double getInitialXPosition() {
        return initialXPosition;
    }

    public double getInitialYPosition() {
        return initialYPosition;
    }

    public double getAngle() {
        return this.angle;
    }

    public double getSpeed() {
        return this.speed;
    }
}
