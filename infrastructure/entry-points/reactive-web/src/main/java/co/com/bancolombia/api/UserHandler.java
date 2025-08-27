package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.api.dto.ResponseUserDto;
import co.com.bancolombia.api.helper.RequestValidator;
import co.com.bancolombia.api.mapper.UserDtoMapper;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserUseCase userUseCase;
    private final UserDtoMapper userDtoMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> listenSave(ServerRequest serverRequest) {
        log.info("Save user called");
        return serverRequest.bodyToMono(CreateUserDto.class)
                .flatMap(requestValidator::validator)
                .flatMap(createUserDto -> (userUseCase.save(userDtoMapper.toUser(createUserDto))
                        .flatMap(user -> {

                            return ServerResponse.status(201)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(new ResponseUserDto("User created successfuly", "201", user));
                        }))).log();

    }

    public Mono<ServerResponse> listenGetByEmail(ServerRequest serverRequest) {
        log.info("Get by Email called");
        String email = serverRequest.pathVariable("email");
        return userUseCase.findUserByEmail(email)
                .flatMap(user -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build()).log();
    }

    public Mono<ServerResponse> listenGetAll(ServerRequest serverRequest) {
        log.info("Get all called");
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(userUseCase.findAll(), User.class);
    }


}
