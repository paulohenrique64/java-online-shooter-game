package com.brothers.shooter_game.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Session implements org.springframework.session.Session {
    private String id;
    private String username;
    private Instant creationTime;

    public Session(String id, String username) {
        this.id = id;
        this.username = username;
        this.creationTime = new Date().toInstant();
    }

    public Session(String username) {
        this.changeSessionId();
        this.username = username;
        this.creationTime = new Date().toInstant();
    }

    @Override
    @JsonManagedReference
    public String getId() {
        return this.id;
    }

    @Override
    public String changeSessionId() {
        this.id = UUID.randomUUID().toString();
        return this.id;
    }

    @Override
    public <T> T getAttribute(String attributeName) {
        return null;
    }

    @Override
    public Set<String> getAttributeNames() {
        Set<String> set = new HashSet<>();

        set.add("id");
        set.add("username");
        set.add("creationTime");

        return set;
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
//        this.setAttribute(attributeName, attributeValue);
    }

    @Override
    public void removeAttribute(String attributeName) {
//        this.removeAttribute(attributeName);
    }

    @Override
    @JsonManagedReference
    public Instant getCreationTime() {
        return this.creationTime;
    }

    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {

    }

    @Override
    public Instant getLastAccessedTime() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(Duration interval) {

    }

    @Override
    public Duration getMaxInactiveInterval() {
        return null;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonManagedReference
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
