package co.com.bancolombia.api;

import co.com.bancolombia.api.auth.AuthHandler;
import co.com.bancolombia.api.auth.config.AuthPath;
import co.com.bancolombia.api.user.config.UserPath;
import co.com.bancolombia.api.user.dto.CreateUserDto;
import co.com.bancolombia.api.dto.ResponseDto;
import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.api.user.dto.ExistsByDocumentAndEmailDto;
import co.com.bancolombia.model.dto.MultipleErrorsResponseDto;
import co.com.bancolombia.model.dto.SingleErrorResponseDto;
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
    private final AuthPath authPath;

    @RouterOperations(

            value = {
                    @RouterOperation(method = POST, path = "/api/v1/usuarios",
                            operation = @Operation(operationId = "save", summary = "Save User", tags = {"Users"},
                                    responses = {
                                            @ApiResponse(responseCode = "201", description = "Successful save", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
                                            , @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MultipleErrorsResponseDto.class)))
                                            , @ApiResponse(responseCode = "409", description = "Email or Document registered already", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SingleErrorResponseDto.class)))
                                            , @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SingleErrorResponseDto.class)))
                                            , @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json"))
                                            , @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json"))

                                    },
                                    requestBody = @RequestBody(
                                            required = true,
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserDto.class))
                                    )


                            )),
                    @RouterOperation(method = GET, path = "/api/v1/usuarios",
                            operation = @Operation(operationId = "findAll", tags = "Users", summary = "Get all Users",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "Successful retrieve", content = @Content(mediaType = "text/event-stream", schema = @Schema(implementation = ResponseDto.class)))
                                            , @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SingleErrorResponseDto.class)))
                                            , @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json"))
                                            , @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json"))

                                    },
                                    requestBody = @RequestBody(
                                            required = true,
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExistsByDocumentAndEmailDto.class))
                                    )

                            )),

                    @RouterOperation(method = POST, path = "/api/v1/usuarios/exists",
                            operation = @Operation(operationId = "existsByDocumentAndEmail", tags = "Users", summary = "Exist by email and document",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "Successful retrieve", content = @Content(mediaType = "text/event-stream", schema = @Schema(implementation = ResponseDto.class)))
                                            , @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json"))
                                            , @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json"))

                                    }

                            )),

                    @RouterOperation(method = GET, path = "/api/v1/login",
                            operation = @Operation(operationId = "login", tags = "Authentication", summary = "Generate Token",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "Successful generation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
                                            , @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SingleErrorResponseDto.class)))
                                            , @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MultipleErrorsResponseDto.class)))
                                    }

                            )),

                    @RouterOperation(method = GET, path = "/api/v1/auth/same-email",
                            operation = @Operation(operationId = "Auth", tags = "Authentication", summary = "Verify that the users header and body sent is equals",
                                    responses = {
                                            @ApiResponse(responseCode = "200", description = "Is not the same email as token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class)))
                                            , @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json"))
                                            , @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MultipleErrorsResponseDto.class)))
                                            , @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json"))
                                    }

                            )),

            }

    )
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler, AuthHandler authHandler) {
        return route(POST(userPath.getUsers()), userHandler::listenSave)
                .andRoute(POST(userPath.getExistsByDocumentAndEmail()), userHandler::listenExistsByDocumentAndEmail)
                .and(route(GET(userPath.getUsers()), userHandler::listenGetAll))
                /* Auth Path*/
                .andRoute(POST(authPath.getLogin()), authHandler::listenLogin)
                .andRoute(POST(authPath.getIsSameEmailAsToken()), authHandler::isSameEmailAsToken)
                ;
    }
}
