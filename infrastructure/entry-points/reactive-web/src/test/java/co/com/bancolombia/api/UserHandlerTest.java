package co.com.bancolombia.api;

import co.com.bancolombia.api.helper.RequestValidator;
import co.com.bancolombia.api.user.mapper.UserDtoMapper;
import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;


@ContextConfiguration(classes = {UserHandler.class})
@WebFluxTest
class UserHandlerTest {


    WebTestClient client;

    @BeforeEach
    void setUp(ApplicationContext context) {
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    @MockitoBean
    private RequestValidator requestValidator;

    @Test
    void listenSave_ShouldCreateUser_whenUserDoesNotExist() {
    }

    @Test
    void listenGetByEmail() {
    }

    @Test
    void listenGetAll() {
    }
}