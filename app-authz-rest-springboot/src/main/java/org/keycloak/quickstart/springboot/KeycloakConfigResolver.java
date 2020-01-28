package org.keycloak.quickstart.springboot;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
public class KeycloakConfigResolver extends KeycloakSpringBootConfigResolver {
}
