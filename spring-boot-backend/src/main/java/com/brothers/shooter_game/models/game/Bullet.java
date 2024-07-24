package com.brothers.shooter_game.models.game;

import java.awt.*;
import java.awt.geom.Point2D;

public class Bullet {
    private Point2D.Double position;
    private double angle;
    private double speed;
    private String shooterName;

    public Bullet(Point2D.Double position, double angle, double speed, String shooterName) {
        this.position = position;
        this.angle = angle;
        this.speed = speed;
        this.shooterName = shooterName;
    }

    public Bullet(Point2D.Double position, double angle, String shooterName) {
        this.position = position;
        this.angle = angle;
        this.speed = 40;
        this.shooterName = shooterName;
    }

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getShooterName() {
        return shooterName;
    }

    public void setShooterName(String shooterName) {
        this.shooterName = shooterName;
    }
}
