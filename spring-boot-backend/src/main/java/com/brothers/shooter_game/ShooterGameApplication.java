package com.brothers.shooter_game;

import com.brothers.shooter_game.repository.SessionRepository;
import com.brothers.shooter_game.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

//import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMongoRepositories
public class ShooterGameApplication implements CommandLineRunner {
	@Autowired
	UserRepository userRepo;

	@Autowired
	SessionRepository sessionRepo;

	public static void main(String[] args) {
		SpringApplication.run(ShooterGameApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		for (int i = 0; i < 5; i++) System.out.println("hello world");
		System.out.println("aplication BACKEND running on: http://localhost:8080" );
		System.out.println("aplication FRONTEND running on: http://localhost:3000" );
		sessionRepo.deleteAll();
//		userRepo.deleteAll(); // delete all users from userdata database
	}
}
