package com.brothers.shooter_game.controllers;

import com.brothers.shooter_game.models.auth.User;
import com.brothers.shooter_game.models.game.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://127.0.0.1:5500")
@RestController
public class UserController {

    @GetMapping("/userdata")
    public ResponseEntity getUserData(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        User userdata = (User) usernamePasswordAuthenticationToken.getPrincipal();
        return ResponseEntity.ok(new UserDTO(userdata));
    }
}
