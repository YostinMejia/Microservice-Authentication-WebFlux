package co.com.bancolombia.api;

import co.com.bancolombia.api.config.UserPath;
import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.api.dto.ResponseUserDto;
import co.com.bancolombia.model.user.exceptions.MultipleErrorsResponseDto;
import co.com.bancolombia.model.user.exceptions.SingleErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final UserPath userPath;
    private final UserHandler userHandler;

    @RouterOperations(

            value = {
                    @RouterOperation(method = POST, path = "/api/v1/usuarios",
                            operation = @Operation(operationId = "save", summary = "Save User", tags = {"Users"},
                                    responses = {
                                            @ApiResponse(responseCode = "201", description = "Successful save", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUserDto.class)))
                                            , @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MultipleErrorsResponseDto.class)))
                                            , @ApiResponse(responseCode = "409", description = "Email registered already", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SingleErrorResponseDto.class)))
                                            , @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SingleErrorResponseDto.class)))
                                    },
                                    requestBody = @RequestBody(
                                            required = true,
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserDto.class))
                                    )


                            )),
                    @RouterOperation(method = GET, path = "/api/v1/usuarios",
                            operation = @Operation(operationId = "findAll", tags = "Users", summary = "Get all Users",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "Successful retrieve", content = @Content(mediaType = "text/event-stream", schema = @Schema(implementation = ResponseUserDto.class)))
                                    }

                            )),

            }

    )
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route(POST(userPath.getUsers()), userHandler::listenSave)
                .andRoute(GET(userPath.getExistsByDocument()), userHandler::listenGetByDocument)
                .and(route(GET(userPath.getUsers()), userHandler::listenGetAll));
    }
}
