package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.api.mapper.UserDtoMapper;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final UserDtoMapper userDtoMapper;
    private final Validator validator;


    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreateUserDto.class)
                .flatMap(createUserDto -> {
                    Set<ConstraintViolation<CreateUserDto>> errors = validator.validate(createUserDto);
                    if (errors.isEmpty()) {
                        return ServerResponse.status(201)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(userUseCase.save(userDtoMapper.toUser(createUserDto)), User.class);
                    }

                    return Mono.error(new ValidationException(
                            errors.stream()
                                    .map(error -> String.format("%s: %s", error.getPropertyPath(), error.getMessage()))
                                    .collect(Collectors.joining("; "))
                    ));

//                            ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(errors.stream().map(error -> String.format("%s: %s", error.getPropertyPath(), error.getMessage())).toList());

                });

    }

    public Mono<ServerResponse> listenGetUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");
        return userUseCase.findUserByEmail(email)
                .flatMap(user -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(userUseCase.findAll(), User.class);
    }
}
