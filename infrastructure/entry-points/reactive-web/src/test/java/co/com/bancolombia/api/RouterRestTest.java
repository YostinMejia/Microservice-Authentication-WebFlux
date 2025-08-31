package co.com.bancolombia.api;

import co.com.bancolombia.api.user.config.UserPath;
import co.com.bancolombia.api.dto.ResponseUserDto;
import co.com.bancolombia.api.helper.RequestValidator;
import co.com.bancolombia.api.user.mapper.UserDtoMapper;
import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.exceptions.MultipleErrorsResponseDto;
import co.com.bancolombia.model.exceptions.SingleErrorResponseDto;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = {UserHandler.class, RouterRest.class, UserPath.class})
@TestPropertySource(properties = {
        "routes.paths.users.users=/api/v1/usuarios",
        "routes.paths.users.findByEmail=/api/v1/usuarios/{email}"
})
@WebFluxTest
public class RouterRestTest {

    WebTestClient client;

    @BeforeEach
    void setUp(ApplicationContext context) {
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @MockitoBean
    private UserHandler userHandler;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    @MockitoBean
    private RequestValidator requestValidator;

    @Test
    void listenSaveUseCase_whenOccursInternalError_thenShouldReturnSingleErrorDto() {

        SingleErrorResponseDto responseBody = new SingleErrorResponseDto("Internal Server Error", "I500-00");
        given(userHandler.listenSave(any())).willReturn(ServerResponse.status(500).contentType(MediaType.APPLICATION_JSON).bodyValue(responseBody));

        client.post()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(SingleErrorResponseDto.class)
                .isEqualTo(responseBody);
    }

    @Test
    void listenSaveUseCase_whenTheCreateUserDtoFailed_thenShouldReturnMultipleErrorDto() {

        MultipleErrorsResponseDto responseBody = new MultipleErrorsResponseDto(
                List.of("document: no debe estar vacío",
                "baseSalary: no debe ser nulo",
                "document: no debe ser nulo"), "Create user validation failed", "B400-00");
        given(userHandler.listenSave(any())).willReturn(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(responseBody));

        client.post()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(MultipleErrorsResponseDto.class)
                .isEqualTo(responseBody);
    }

    @Test
    void listenSaveUseCase_whenTheUserIsRegistered_thenShouldReturnErrorDto() {

        SingleErrorResponseDto responseBody = new SingleErrorResponseDto("User registered already", "B400-00");
        given(userHandler.listenSave(any())).willReturn(ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(responseBody));

        client.post()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(SingleErrorResponseDto.class)
                .isEqualTo(responseBody);
    }

    @Test
    void listenSaveUseCase_whenTheUserDoesNotExist_thenShouldReturnResponseDto() {

        User user = User.builder()
                .name("Alice")
                .lastName("Smith")
                .birthDate(LocalDate.of(1995, 5, 20))
                .address("Calle 123")
                .phone("3001234567")
                .email("alice@mail.com")
                .baseSalary("2000")
                .document("984123412")
                .build();
        ResponseUserDto responseBody = new ResponseUserDto("User created successfully", "201-00", user);
        given(userHandler.listenSave(any())).willReturn(ServerResponse.status(201).contentType(MediaType.APPLICATION_JSON).bodyValue(responseBody));

        client.post()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseUserDto.class)
                .isEqualTo(responseBody);
    }
}
