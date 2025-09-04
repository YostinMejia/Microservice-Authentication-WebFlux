package co.com.bancolombia.api.auth.jwt;

import co.com.bancolombia.usecase.auth.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthUseCase authUseCase;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        return authUseCase.isValidToken(token)
                .map(authorizedUser ->
                        new UsernamePasswordAuthenticationToken(authorizedUser,
                                token,
                                List.of(new SimpleGrantedAuthority("ROLE_" + authorizedUser.rol())))

                );
    }
}
