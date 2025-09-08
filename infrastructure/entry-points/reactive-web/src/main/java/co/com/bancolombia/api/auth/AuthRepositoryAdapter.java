package co.com.bancolombia.api.auth;

import co.com.bancolombia.api.auth.config.JWTConfig;
import co.com.bancolombia.model.auth.AuthorizedUser;
import co.com.bancolombia.model.auth.gateways.AuthRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Repository
public class AuthRepositoryAdapter implements AuthRepository {

    private final SecretKey key;
    private final Duration tokenValidity;

    public AuthRepositoryAdapter(JWTConfig jwtConfig) {
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        this.tokenValidity = Duration.ofMinutes(jwtConfig.getExpirationMinutes());
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public Mono<String> generateToken(AuthorizedUser authorizedUser) {
        return Mono.just(Jwts.builder()
                .claims(Map.of(TokenClaims.EMAIL.getValue(), authorizedUser.email(), TokenClaims.ROL.getValue(), authorizedUser.rol()))
                .subject(authorizedUser.document())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(tokenValidity)))
                .signWith(key)
                .compact());
    }

    @Override
    public Mono<AuthorizedUser> isValidToken(String token) {
        final Claims claims = extractAllClaims(token);
        return Mono.just(claims)
                .flatMap(claimsVerified -> Mono.just(
                        new AuthorizedUser(
                                claimsVerified.getSubject(),
                                (String) claimsVerified.get(TokenClaims.EMAIL.getValue()),
                                (String) claimsVerified.get(TokenClaims.ROL.getValue()))
                ));
    }

    @Override
    public Mono<String> getTokenEmail() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    AuthorizedUser authorizedUser = (AuthorizedUser) authentication.getPrincipal();
                    return authorizedUser.email();
                });
    }

}


