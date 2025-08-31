package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    public Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> existsByDocument(String document) {
        return userRepository.existsByDocument(document);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> save(User user, String rol) {

        return userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument())
                .flatMap(isRegistered -> {
                    if (isRegistered){
                        return Mono.error(new BusinessException(null, "User registered already", "B400-00"));
                    }
                    return rolRepository.findByName(rol);
                })
                .switchIfEmpty(Mono.error(new BusinessException(null, "Rol does not exist", "B400-00")))
                .map(rolRetrieved->user.toBuilder().idRol(rolRetrieved.getId()).build())
                .flatMap(userRepository::save);

    }

}
