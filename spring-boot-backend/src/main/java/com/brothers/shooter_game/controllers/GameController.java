package com.brothers.shooter_game.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("game")
public class GameController {
    @GetMapping("/")
    private ResponseEntity startGame() {
        return ResponseEntity.ok().build();
    }
}
