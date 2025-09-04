package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.user.gateways.PasswordCryptoGateway;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.utils.BusinessErrorCode;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordCryptoGateway passwordCryptoGateway;

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> existsByDocumentAndEmail(String document, String email) {
        return userRepository.existsByDocumentAndEmail(document, email);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> validateCredentials(String email, String password) {
        return this.findByEmail(email)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessErrorCode.USER_NOT_FOUND)))
                .filter(user ->  passwordCryptoGateway.matches(user.getPassword(), password))
                .switchIfEmpty(Mono.error(new BusinessException(BusinessErrorCode.INVALID_CREDENTIALS)));
    }

    public Mono<User> save(User user, String rol) {

        return userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument())
                .flatMap(isRegistered -> {
                    if (isRegistered) {
                        return Mono.error(new BusinessException(BusinessErrorCode.USER_ALREADY_REGISTERED));
                    }
                    return rolRepository.findByName(rol);
                })
                .switchIfEmpty(Mono.error(new BusinessException(BusinessErrorCode.ROL_NOT_EXIST)))
                .map(rolRetrieved -> user.toBuilder()
                        .idRol(rolRetrieved.getId())
                        .password(passwordCryptoGateway.encode(user.getPassword()))
                        .build())
                .flatMap(userRepository::save);

    }

}
