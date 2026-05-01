package com.dev.simplespa.component.controller.dto;

public class UserPrincipal {
    private final int id;
    private final String username;

    public UserPrincipal(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
}
