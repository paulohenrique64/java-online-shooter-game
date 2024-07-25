package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.models.others.Greeting;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@CrossOrigin("http://127.0.0.1:3000")
@RestController
public class GreetingsController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name, HttpSession session) {
        return new Greeting(counter.incrementAndGet(), session.getId());
    }
}
