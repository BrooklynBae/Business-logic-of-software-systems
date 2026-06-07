package com.blps_lab1.demo.security;

import java.security.Principal;

public record JaasRolePrincipal(String name) implements Principal {
    @Override
    public String getName() {
        return name;
    }
}
