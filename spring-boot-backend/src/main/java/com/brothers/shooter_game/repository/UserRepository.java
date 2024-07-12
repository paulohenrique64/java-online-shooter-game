package com.brothers.shooter_game.repository;

import com.brothers.shooter_game.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    UserDetails findByName(String name);
}
