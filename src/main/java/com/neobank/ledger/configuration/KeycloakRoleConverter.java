package com.neobank.ledger.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Obtenemos el objeto "resource_access"
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        // Entramos a "react" (tu client-id) y luego a "roles"
        Map<String, Object> reactClient = (Map<String, Object>) resourceAccess.get("react");
        Collection<String> roles = (Collection<String>) reactClient.get("roles");

        // Convertimos a GrantedAuthority con el prefijo ROLE_
        return roles.stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}