package co.com.bancolombia.api.auth.config;

import co.com.bancolombia.api.auth.jwt.JwtAuthenticationManager;
import co.com.bancolombia.api.auth.jwt.JwtSecurityContextRepository;
import co.com.bancolombia.api.user.config.UserPath;
import co.com.bancolombia.model.utils.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationManager authenticationManager;
    private final JwtSecurityContextRepository securityContextRepository;
    private final UserPath userPath;
    private final AuthPath authPath;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers(HttpMethod.POST, userPath.getUsers()).hasAnyRole(Roles.ADVISER.getValue(), Roles.ADMIN.getValue())
                                .pathMatchers(userPath.getExistsByDocumentAndEmail()).hasRole(Roles.CLIENT.getValue())
                                .pathMatchers(userPath.getFindRoleNameByEmail()).hasRole(Roles.ADVISER.getValue())
                                .pathMatchers(authPath.getIsSameEmailAsToken()).hasAnyRole(Roles.CLIENT.getValue(), Roles.ADMIN.getValue(), Roles.ADVISER.getValue())
                                .pathMatchers(authPath.getLogin()).permitAll()
                                .anyExchange().permitAll()
                )
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}
