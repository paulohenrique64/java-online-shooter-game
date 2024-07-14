package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.Models.User;
import com.brothers.shooter_game.Models.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @GetMapping("/userdata")
    public ResponseEntity getUserData(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken, HttpServletResponse response) {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();

        return ResponseEntity.ok(new UserDTO(userdata));
    }
}
