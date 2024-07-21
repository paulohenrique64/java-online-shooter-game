package com.brothers.shooter_game.repository;

import com.brothers.shooter_game.models.auth.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends MongoRepository<User, String> {
    UserDetails findByName(String name);
}
