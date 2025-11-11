package org.mobilehub.payment_service.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EnvConfig {
    private final ConfigurableEnvironment environment;

    public EnvConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        Map<String, Object> envMap = new HashMap<>();
        dotenv.entries().forEach(e -> envMap.put(e.getKey(), e.getValue()));

        environment.getPropertySources()
                .addFirst(new MapPropertySource("dotenvProperties", envMap));

        System.out.println("✅ .env loaded successfully (" + envMap.size() + " variables)");
    }
}
