package com.brothers.shooter_game.models.auth;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document("userdata")
public class User implements UserDetails {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String password;
    private int kills;
    private int gamesPlayed;
    private int wins;
    private double score;
    private Date created;
    private UserRoles role;

    public User() {
    }

    public User(String name, String password, UserRoles role) {
        this.password = password;
        this.name = name;
        this.created = new Date();
        this.kills = 0;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.score = 0;
        this.role = role;
    }

    public User(String name, String password, UserRoles role, int kills, int gamesPlayed, int wins, int score, Date created) {
        this.name = name;
        this.password = password;
        this.kills = kills;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.score = score;
        this.created = created;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRoles.ROLE_ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Date getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", kills=" + kills +
                ", gamesPlayed=" + gamesPlayed +
                ", wins=" + wins +
                ", score=" + score +
                ", created=" + created +
                ", role=" + role +
                '}';
    }
}
