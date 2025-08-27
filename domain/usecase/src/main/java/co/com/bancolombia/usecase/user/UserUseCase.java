package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exceptions.BusinessException;
import co.com.bancolombia.model.user.exceptions.ErrorResponseDto;
import co.com.bancolombia.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> save(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(userFound -> Mono.<User>error(new BusinessException(new ErrorResponseDto(List.of("User registered already"), "User registered already", "B400-00"))))
                .switchIfEmpty(userRepository.save(user));
    }


}
