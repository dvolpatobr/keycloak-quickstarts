package org.keycloak.quickstart.springboot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfigResolver extends KeycloakSpringBootConfigResolver {

    private Map<String, KeycloakDeployment> tenants = new HashMap<String, KeycloakDeployment>();

    @Override
    public KeycloakDeployment resolve(HttpFacade.Request request) {
        String path = request.getRelativePath();
        String[] tenantIdx = path.split("/");

        if (tenantIdx.length > 1) {
            String tenantId = tenantIdx[1];
            KeycloakDeployment tenant = tenants.get(tenantId);
            
            if (tenant != null) {
                return tenant;
            }
            
            synchronized (tenants) {
                return tenants.computeIfAbsent(tenantId, new Function<String, KeycloakDeployment>() {
                    public KeycloakDeployment apply(String tenant) {
                        return resolveTenant(tenant);
                    }
                });
            }
        }

        throw new RuntimeException("Invalid tenant");
    }

    private KeycloakDeployment resolveTenant(String tenant) {
        if ("realm-a".equals(tenant)) {
            return createTenantDeployment(tenant, "cc5751be-9398-422b-85c8-237563c37600");
        } else if ("realm-b".equals(tenant)) {
            return createTenantDeployment(tenant, "635691e2-f698-4df5-bed1-1f3c90f97815");
        }

        throw new RuntimeException("Invalid tenant");
    }

    private KeycloakDeployment createTenantDeployment(String tenant, String clientSecret) {
        AdapterConfig config = new AdapterConfig();

        config.setAuthServerUrl("http://localhost:8180/auth");
        config.setRealm(tenant);
        config.setResource(tenant + "-client");

        Map<String, Object> credentials = new HashMap<String, Object>();

        credentials.put("secret", clientSecret);

        config.setCredentials(credentials);

        PolicyEnforcerConfig enforcerConfig = new PolicyEnforcerConfig();

        enforcerConfig.setEnforcementMode(PolicyEnforcerConfig.EnforcementMode.PERMISSIVE);

        config.setPolicyEnforcerConfig(enforcerConfig);

        return KeycloakDeploymentBuilder.build(config);
    }
}
