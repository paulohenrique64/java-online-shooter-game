package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.models.auth.User;
import com.brothers.shooter_game.models.game.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @GetMapping("/userdata")
    public ResponseEntity getUserData(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        return ResponseEntity.ok(new UserDTO(userdata));
    }
}
