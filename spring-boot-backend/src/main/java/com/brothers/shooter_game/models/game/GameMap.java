package com.brothers.shooter_game.models.game;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameMap {
    private final List<List<Integer>> map = new ArrayList<>();
    private final List<Point> positionWall = new ArrayList<Point>();
    private final List<Point> respawnArea = new ArrayList<Point>();

    public GameMap() {
        // map 1
        this.map.add(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        this.map.add(Arrays.asList(1, 0, 0, 0, 0, 0, 0, 0, 0, 1));
        this.map.add(Arrays.asList(1, 0, 1, 1, 0, 0, 1, 1, 0, 1));
        this.map.add(Arrays.asList(1, 0, 0, 0, 0, 0, 0, 0, 0, 1));
        this.map.add(Arrays.asList(1, 0, 1, 0, 1, 1, 0, 1, 0, 1));
        this.map.add(Arrays.asList(1, 0, 0, 1, 0, 0, 0, 0, 0, 1));
        this.map.add(Arrays.asList(1, 0, 0, 0, 0, 0, 1, 0, 0, 1));
        this.map.add(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

        // calculate the map position wall
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(0).size(); j++) {
                if (this.map.get(i).get(j) == 1) {
                    int tileSize = 110;
                    positionWall.add(new Point(j * tileSize, i * tileSize));
                }
            }
        }

        // calculate the map respawn area
        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(0).size(); j++) {
                if (this.map.get(i).get(j) == 0) {
                    int tileSize = 110;
                    respawnArea.add(new Point(j * tileSize, i * tileSize));
                }
            }
        }
    }

    public List<List<Integer>> getMap() {
        return map;
    }

    public List<Point> getPositionWall() {
        return positionWall;
    }

    public List<Point> getRespawnArea() {
        return respawnArea;
    }
}
