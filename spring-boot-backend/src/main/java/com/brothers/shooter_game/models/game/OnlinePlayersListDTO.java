package com.brothers.shooter_game.models.game;

import com.brothers.shooter_game.models.auth.Session;

import java.util.List;

public record OnlinePlayersListDTO(List<Session> sessions) {
}
