package co.com.bancolombia.api.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths.users")
public class UserPath {
    private String users;
    private String findByEmail;
    private String existsByDocument;
}
