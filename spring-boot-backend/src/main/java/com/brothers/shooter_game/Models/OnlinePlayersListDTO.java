package com.brothers.shooter_game.Models;

import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.List;

public record OnlinePlayersListDTO(List<Session> sessions) {
}
