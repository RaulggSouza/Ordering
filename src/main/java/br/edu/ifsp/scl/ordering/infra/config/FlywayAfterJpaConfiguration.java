package br.edu.ifsp.scl.ordering.infra.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnClass(Flyway.class)
public class FlywayAfterJpaConfiguration {

    @Bean
    FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {};
    }

    @Bean
    @Profile("local")
    @ConditionalOnProperty(name = "ordering.flyway.migrate-after-jpa", havingValue = "true", matchIfMissing = true)
    ApplicationRunner flywayAfterJpaRunner(Flyway flyway) {
        return args -> flyway.migrate();
    }
}
