package co.com.bancolombia.usecase.auth;

import co.com.bancolombia.model.auth.AuthorizedUser;
import co.com.bancolombia.model.auth.gateways.AuthRepository;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @InjectMocks
    private AuthUseCase authUseCase;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UserUseCase userUseCase;

    private User user;
    private Rol rol;
    private AuthorizedUser authorizedUser;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .document("123456")
                .email("test@example.com")
                .password("encodedPass")
                .idRol(UUID.randomUUID())
                .build();

        rol = Rol.builder()
                .id(user.getIdRol())
                .name("CLIENT")
                .description("Client role")
                .build();

        authorizedUser = new AuthorizedUser(user.getDocument(), user.getEmail(), rol.getName());
    }


    @Test
    void login_whenCredentialsAreValid_thenShouldReturnToken() {
        // Arrange
        String token = "jwt-token";
        given(userUseCase.validateCredentials(user.getEmail(), "rawPass")).willReturn(Mono.just(user));
        given(rolRepository.findById(user.getIdRol())).willReturn(Mono.just(rol));
        given(authRepository.generateToken(authorizedUser)).willReturn(Mono.just(token));

        // Act & Assert
        StepVerifier.create(authUseCase.login(user.getEmail(), "rawPass"))
                .expectNext(token)
                .verifyComplete();
    }

    @Test
    void login_whenRolNotFound_thenShouldReturnEmpty() {
        // Arrange
        given(userUseCase.validateCredentials(user.getEmail(), "rawPass")).willReturn(Mono.just(user));
        given(rolRepository.findById(user.getIdRol())).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(authUseCase.login(user.getEmail(), "rawPass"))
                .verifyComplete(); // no emission because rol is missing
    }


    @Test
    void isValidToken_whenTokenIsValid_thenShouldReturnAuthorizedUser() {
        // Arrange
        String token = "valid-token";
        given(authRepository.isValidToken(token)).willReturn(Mono.just(authorizedUser));

        // Act & Assert
        StepVerifier.create(authUseCase.isValidToken(token))
                .expectNext(authorizedUser)
                .verifyComplete();
    }

    @Test
    void isValidToken_whenTokenIsInvalid_thenShouldReturnEmpty() {
        // Arrange
        String token = "invalid-token";
        given(authRepository.isValidToken(token)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(authUseCase.isValidToken(token))
                .verifyComplete(); // no AuthorizedUser returned
    }


    @Test
    void isSameEmailAsToken_whenEmailMatches_thenShouldReturnTrue() {
        // Arrange
        String email = "test@example.com";
        given(authRepository.isSameEmailAsToken(email)).willReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(authUseCase.isSameEmailAsToken(email))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isSameEmailAsToken_whenEmailDoesNotMatch_thenShouldReturnFalse() {
        // Arrange
        String email = "wrong@example.com";
        given(authRepository.isSameEmailAsToken(email)).willReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(authUseCase.isSameEmailAsToken(email))
                .expectNext(false)
                .verifyComplete();
    }
}
