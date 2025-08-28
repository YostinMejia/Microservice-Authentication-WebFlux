package co.com.bancolombia.api;

import co.com.bancolombia.api.config.UserPath;
import co.com.bancolombia.api.dto.ResponseUserDto;
import co.com.bancolombia.api.helper.RequestValidator;
import co.com.bancolombia.api.mapper.UserDtoMapper;
import co.com.bancolombia.model.user.User;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = { UserHandler.class, RouterRest.class, UserPath.class})
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
    void listenGETUseCase_whenNoUserIsRegistered_thenShouldReturnEmptyBody() {
        given(userHandler.listenGetAll(any())).willReturn(ServerResponse.ok().build());

        client.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    void listenGETUseCase_whenNoUserIsRegistered_thenShouldReturnResponseDto() {

        User user1 = User.builder()
                .name("Alice")
                .lastName("Smith")
                .birthDate(LocalDate.of(1995, 5, 20))
                .address("Calle 123")
                .phone("3001234567")
                .email("alice@mail.com")
                .baseSalary("2000")
                .build();
        ResponseUserDto responseBody =  new ResponseUserDto("User created successfully", "201", user1);
        given(userHandler.listenGetAll(any())).willReturn(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(responseBody));

        client.get()
                .uri("/api/v1/usuarios")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }
}
