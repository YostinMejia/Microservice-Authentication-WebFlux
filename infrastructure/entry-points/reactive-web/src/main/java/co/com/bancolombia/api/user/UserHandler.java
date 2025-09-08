package co.com.bancolombia.api.user;

import co.com.bancolombia.api.helper.ResponseMapper;
import co.com.bancolombia.api.user.dto.CreateUserDto;
import co.com.bancolombia.api.helper.RequestValidator;
import co.com.bancolombia.api.user.dto.ExistsByDocumentAndEmailDto;
import co.com.bancolombia.api.user.dto.UserDataResponseDto;
import co.com.bancolombia.api.user.mapper.UserDtoMapper;
import co.com.bancolombia.model.utils.BusinessErrorCode;
import co.com.bancolombia.model.utils.LogMessage;
import co.com.bancolombia.model.utils.ResponseCode;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        log.info(LogMessage.SAVE_USER_CALLED.getMessage());

        return serverRequest.bodyToMono(CreateUserDto.class)
                .flatMap(requestValidator::validator)
                .flatMap(createUserDto -> (
                        userUseCase.save(
                                userDtoMapper.toUser(createUserDto),
                                createUserDto.rol()))
                        .map(userDtoMapper::toUserResponseDto)
                        .map(data -> ResponseMapper.mapBodyResponse(ResponseCode.USER_CREATED, data))
                        .flatMap(response ->
                                ServerResponse.status(HttpStatus.CREATED)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(response)
                        ));

    }

    public Mono<ServerResponse> listenExistsByDocumentAndEmail(ServerRequest serverRequest) {
        log.info(LogMessage.EXIST_BY_DOCUMENT_AND_EMAIL_CALLED.getMessage());

        return serverRequest.bodyToMono(ExistsByDocumentAndEmailDto.class)
                .flatMap(body -> userUseCase.existsByDocumentAndEmail(body.document(), body.email()))
                .map(data -> ResponseMapper.mapBodyResponse(data ?
                                ResponseCode.USER_EXISTS : BusinessErrorCode.USER_NOT_FOUND
                        , data))
                .flatMap(response ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response));

    }

    public Mono<ServerResponse> listenGetAll(ServerRequest serverRequest) {
        log.info(LogMessage.GET_ALL_CALLED.getMessage());
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(userUseCase.findAll().map(userDtoMapper::toUserResponseDto), UserDataResponseDto.class);
    }

}
