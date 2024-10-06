package br.com.siswbrasil;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

import java.util.Map;

@Singleton
public class Bootstrap {

    private static final Logger LOGGER = Logger.getLogger(Bootstrap.class);

    @ConfigProperty(name = "quarkus.http.host", defaultValue = "localhost")
    String host;

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
    int port;

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "quarkus-application")
    String appName;

    @ConfigProperty(name = "quarkus.application.version", defaultValue = "1.0.0")
    String appVersion;

    @ConfigProperty(name = "quarkus.profile", defaultValue = "prod")
    String profile;

    void onStart(@Observes StartupEvent ev) {
        String baseUrl = "http://" + host + ":" + port;

        logEnvironmentVariables();
        logApplicationProperties();
        logApplicationInfo();
    }

    private void logEnvironmentVariables() {
        LOGGER.info("############################################");
        LOGGER.info("### Dados de Ambiente:");
        LOGGER.info("############################################");

        Map<String, String> env = System.getenv();
        env.forEach((key, value) -> LOGGER.info(key + ": " + value));
    }

    private void logApplicationProperties() {
        LOGGER.info("############################################");
        LOGGER.info("### Propriedades da Aplicação:");
        LOGGER.info("############################################");

        Config config = ConfigProvider.getConfig();
        config.getPropertyNames().forEach(propertyName -> {
            String propertyValue = config.getOptionalValue(propertyName, String.class).orElse(null);
            if (propertyValue != null && !propertyValue.isEmpty()) {
                LOGGER.info(propertyName + ": " + propertyValue);
            }
        });
    }

    private void logApplicationInfo() {
        LOGGER.info("############################################");
        LOGGER.info("### Informações da Aplicação:");
        LOGGER.info("############################################");
        LOGGER.info("Aplicação: " + appName + " v" + appVersion);
        LOGGER.info("Perfil Ativo: " + profile);
        LOGGER.info("############################################");
    }
}