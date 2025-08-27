package co.com.bancolombia.api;

import co.com.bancolombia.api.config.UserPath;
import co.com.bancolombia.api.dto.ResponseUserDto;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.exceptions.ErrorResponseDto;
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
                    @RouterOperation(method = POST, path = "/api/v1/users",
                            operation = @Operation(operationId = "save", summary = "Save User", tags = {"Users"},
                                    responses = {
                                            @ApiResponse(responseCode = "201", description = "Successful save", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
                                            , @ApiResponse(responseCode = "409", description = "Email registered already", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
                                    },
                                    requestBody = @RequestBody(
                                            required = true,
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUserDto.class))
                                    )


                            )),
                    @RouterOperation(method = GET, path = "/api/v1/users",
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
                .andRoute(GET(userPath.getFindByEmail()), userHandler::listenGetByEmail)
                .and(route(GET(userPath.getUsers()), userHandler::listenGetAll));
    }
}
