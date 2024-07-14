package com.brothers.shooter_game.Models;

import org.springframework.messaging.Message;

import java.util.ArrayList;

public record OnlinePlayersListDTO(ArrayList<Session> sessions) {
}
