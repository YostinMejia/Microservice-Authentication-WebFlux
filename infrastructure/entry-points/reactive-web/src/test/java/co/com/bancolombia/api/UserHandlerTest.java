package co.com.bancolombia.api;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@RequiredArgsConstructor
class UserHandlerTest {

    private final WebTestClient webTestClient;

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