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

        return serverRequest.bodyToMono(CreateUserDto.class)
                .doOnNext(l -> log.info("Save user called"))
                .flatMap(requestValidator::validator).
                map(userDtoMapper::toUser)
                .flatMap(user -> (userUseCase.save(user))
                        .flatMap(userSaved ->
                                ServerResponse.status(201)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(new ResponseUserDto<User>("User created successfully", "201-00", userSaved))
                        ));

    }

    public Mono<ServerResponse> listenGetByDocument(ServerRequest serverRequest) {
        log.info("Get by Email called");
        String document = serverRequest.pathVariable("document");
        return userUseCase.existsByDocument(document)
                .flatMap(exist -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(new ResponseUserDto<Boolean>("User exists","200-00",exist) ))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenGetAll(ServerRequest serverRequest) {
        log.info("Get all called");
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(userUseCase.findAll(), User.class);
    }


}
