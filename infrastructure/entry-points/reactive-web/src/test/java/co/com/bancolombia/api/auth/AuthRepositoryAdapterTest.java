package co.com.bancolombia.api.auth;

import co.com.bancolombia.api.auth.config.JWTConfig;
import co.com.bancolombia.model.auth.AuthorizedUser;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthRepositoryAdapterTest {

    private AuthRepositoryAdapter authRepositoryAdapter;

    private AuthorizedUser authorizedUser;

    @BeforeEach
    void setUp() {
        JWTConfig jwtConfig = new JWTConfig();
        jwtConfig.setSecret("my-super-secret-key-12345678901234567890");
        jwtConfig.setExpirationMinutes(5L);

        authRepositoryAdapter = new AuthRepositoryAdapter(jwtConfig);

        authorizedUser = new AuthorizedUser(
                UUID.randomUUID().toString(),
                "test@example.com",
                "CLIENT"
        );
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        // Act
        Mono<String> tokenMono = authRepositoryAdapter.generateToken(authorizedUser);

        // Assert
        StepVerifier.create(tokenMono)
                .assertNext(token -> {
                    assertThat(token).isNotNull();
                    assertThat(token).contains(".");
                })
                .verifyComplete();
    }

    @Test
    void isValidToken_whenTokenIsValid_shouldReturnAuthorizedUser() {
        // Arrange
        String token = authRepositoryAdapter.generateToken(authorizedUser).block();

        // Act
        Mono<AuthorizedUser> result = authRepositoryAdapter.isValidToken(token);

        // Assert
        StepVerifier.create(result)
                .assertNext(user -> {
                    assertThat(user.document()).isEqualTo(authorizedUser.document());
                    assertThat(user.email()).isEqualTo(authorizedUser.email());
                    assertThat(user.rol()).isEqualTo(authorizedUser.rol());
                })
                .verifyComplete();
    }

    @Test
    void isValidToken_whenTokenIsInvalid_shouldThrowJwtException() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act
        Mono<AuthorizedUser> result = Mono.defer(() -> authRepositoryAdapter.isValidToken(invalidToken));

        // Assert
        StepVerifier.create(result)
                .expectError(JwtException.class)
                .verify();
    }

    @Test
    void isSameEmailAsToken_whenEmailMatches_shouldReturnTrue() {
        // Arrange
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authorizedUser, null);

        Mono<Boolean> result = authRepositoryAdapter.isSameEmailAsToken(authorizedUser.email())
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isSameEmailAsToken_whenEmailDoesNotMatch_shouldReturnFalse() {
        // Arrange
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authorizedUser, null);

        Mono<Boolean> result = authRepositoryAdapter.isSameEmailAsToken("other@example.com")
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}
