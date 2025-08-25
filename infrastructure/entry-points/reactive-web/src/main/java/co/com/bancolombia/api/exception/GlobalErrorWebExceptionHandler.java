package co.com.bancolombia.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;


    GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer, ObjectMapper objectMapper) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
        this.setMessageReaders(serverCodecConfigurer.getReaders());
        this.objectMapper = objectMapper;

    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions
                .route(RequestPredicates.all(), this::handleErrors);

    }

    private Mono<ServerResponse> handleErrors(ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.BINDING_ERRORS,
                        ErrorAttributeOptions.Include.ERROR,
                        ErrorAttributeOptions.Include.STATUS,
                        ErrorAttributeOptions.Include.EXCEPTION,
                        ErrorAttributeOptions.Include.PATH,
                        ErrorAttributeOptions.Include.STACK_TRACE

                ));

        ErrorResponse errorResponse = objectMapper.convertValue(errorPropertiesMap, ErrorResponse.class);
        System.out.println(errorResponse);
        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(errorResponse);

    }


}
