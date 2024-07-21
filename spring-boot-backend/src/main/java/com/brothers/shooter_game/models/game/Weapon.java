package com.brothers.shooter_game.models.game;

import java.util.ArrayList;
import java.util.List;

public class Weapon {
    private double angle;

    public Weapon() {
        this.angle = 0;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }
}
