package br.edu.ifsp.scl.ordering.application.service.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "ordering.auth.jwt")
public class JwtProperties {
    private String secret;
    private String issuer;
    private Duration accessTokenTtl;

    public String secret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String issuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Duration accessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }
}
