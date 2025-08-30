package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exceptions.BusinessException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> existsByDocument(String document) {
        return userRepository.existsByDocument(document);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> save(User user) {

        return userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument())
                .filter(Boolean::booleanValue)
                .flatMap(isRegistered -> Mono.<User>error(new BusinessException(null, "User registered already", "B400-00")))
                .switchIfEmpty(Mono.defer(() -> userRepository.save(user)));
    }

}
