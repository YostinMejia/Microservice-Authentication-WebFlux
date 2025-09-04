package co.com.bancolombia.api.auth;

import co.com.bancolombia.api.auth.dto.LoginDto;
import co.com.bancolombia.api.auth.dto.SameEmailAsTokenDto;
import co.com.bancolombia.api.helper.RequestValidator;
import co.com.bancolombia.api.helper.ResponseMapper;
import co.com.bancolombia.model.utils.BusinessErrorCode;
import co.com.bancolombia.model.utils.LogMessage;
import co.com.bancolombia.model.utils.ResponseCode;
import co.com.bancolombia.usecase.auth.AuthUseCase;
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
public class AuthHandler {
    private final AuthUseCase authUseCase;
    private final RequestValidator requestValidator;


    public Mono<ServerResponse> listenLogin(ServerRequest serverRequest) {
        log.info(LogMessage.LOGIN_CALLED.getMessage());
        return serverRequest.bodyToMono(LoginDto.class)
                .flatMap(requestValidator::validator)
                .flatMap(loginDto -> authUseCase.login(loginDto.email(), loginDto.password()))
                .map(data -> ResponseMapper.mapBodyResponse(ResponseCode.TOKEN_GENERATED, data))
                .flatMap(token -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(token)
                );
    }

    public Mono<ServerResponse> isSameEmailAsToken(ServerRequest serverRequest) {
        log.info(LogMessage.IS_SAME_EMAIL_AS_TOKEN_CALLED.getMessage());
        return serverRequest.bodyToMono(SameEmailAsTokenDto.class)
                .flatMap(requestValidator::validator)
                .flatMap(data -> authUseCase.isSameEmailAsToken(data.email()))
                .map(response -> ResponseMapper.mapBodyResponse(
                        response ? ResponseCode.IS_SAME_EMAIL : BusinessErrorCode.IS_NOT_SAME_EMAIL
                        , response))
                .flatMap(responseDto -> ServerResponse.ok().bodyValue(responseDto));


    }

}
