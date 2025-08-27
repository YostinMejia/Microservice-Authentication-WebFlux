package co.com.bancolombia.api;

import co.com.bancolombia.model.user.exceptions.BusinessException;
import co.com.bancolombia.model.user.exceptions.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer, ObjectMapper objectMapper) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
        this.setMessageReaders(serverCodecConfigurer.getReaders());

    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions
                .route(RequestPredicates.all(), this::handleErrors);

    }

    private Mono<ServerResponse> handleErrors(ServerRequest request) {

        Throwable error = getError(request);
        log.error(error.getMessage());

        if (error instanceof BusinessException bex) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(bex.getErrorResponse());
        }

        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STATUS));

        return ServerResponse.status((int) errorPropertiesMap.getOrDefault("status", 400)).contentType(MediaType.APPLICATION_JSON).bodyValue(new ErrorResponseDto(List.of(error.getMessage()), error.getMessage(), String.valueOf(errorPropertiesMap.getOrDefault("status", "I500-00"))));

    }

}
