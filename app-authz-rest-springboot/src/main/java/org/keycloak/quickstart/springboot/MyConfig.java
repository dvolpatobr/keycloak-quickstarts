package org.keycloak.quickstart.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {
    
    @Bean
    public KeycloakConfigResolver configResolver() {
        return new KeycloakConfigResolver();
    }
}
