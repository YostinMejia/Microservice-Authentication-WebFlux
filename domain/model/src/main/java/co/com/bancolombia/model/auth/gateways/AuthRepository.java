package co.com.bancolombia.model.auth.gateways;

import co.com.bancolombia.model.auth.AuthorizedUser;
import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<String> generateToken(AuthorizedUser authorizedUser);
    Mono<AuthorizedUser> isValidToken(String token);
    Mono<String> getTokenEmail();
}
