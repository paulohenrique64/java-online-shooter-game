package com.brothers.shooter_game.contollers;

import com.brothers.shooter_game.Models.*;
import com.brothers.shooter_game.repository.UserRepository;
import com.brothers.shooter_game.services.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody LoginDTO userdata, HttpServletResponse response) {
        System.out.println(userdata);

        if (this.userRepo.findByName(userdata.name()) == null) {
            System.out.println("user not exists");
            return ResponseEntity.badRequest().build();
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(userdata.name(), userdata.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        System.out.println("new user get logged: " + token);

        // set accessToken to cookie header
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
//                .maxAge(cookieExpiry)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody RegisterDTO userdata) {
        if (this.userRepo.findByName(userdata.name()) != null) {
            System.out.println("user already registered");
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(userdata.password());
        User newUser = new User(userdata.name(), encryptedPassword, UserRoles.ROLE_USER);

        userRepo.save(newUser);
        System.out.println("new user registered: " + newUser);

        return ResponseEntity.ok().build();
    }
}

