package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.AuthorizedUser;
import co.com.bancolombia.model.auth.gateways.AuthRepository;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {

    private final AuthRepository authRepository;
    private final RolRepository rolRepository;
    private final UserUseCase userUseCase;

    public Mono<String> login(String email, String password) {

        return userUseCase.validateCredentials(email, password)
                .zipWhen(user -> rolRepository.findById(user.getIdRol()))
                .flatMap(tuple -> Mono.just(new AuthorizedUser(tuple.getT1().getDocument(),
                        tuple.getT1().getEmail(), tuple.getT2().getName())))
                .flatMap(authRepository::generateToken);
    }

    public Mono<AuthorizedUser> isValidToken(String token) {
        return authRepository.isValidToken(token);
    }

    public Mono<Boolean> isSameEmailAsToken(String email) {
        return authRepository.isSameEmailAsToken(email);
    }
}
