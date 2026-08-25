package com.mohammad.userandtaskmanagementsystem.dto;

public class RoleResponse {

    private Long id;
    private String name;

    public RoleResponse() {
    }

    public RoleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
