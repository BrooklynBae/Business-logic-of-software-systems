package com.blps_lab1.demo.security;

import java.security.Principal;

public record JaasAuthorityPrincipal (String name) implements Principal {
    @Override
    public String getName() {
        return name;
    }
}
