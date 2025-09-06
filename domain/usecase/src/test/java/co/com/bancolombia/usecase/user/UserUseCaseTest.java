package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.PasswordCryptoGateway;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.utils.BusinessErrorCode;
import co.com.bancolombia.model.utils.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @InjectMocks
    private UserUseCase userUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordCryptoGateway passwordCryptoGateway;

    private User user;
    private Rol clientRol;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("Juan")
                .lastName("Pérez")
                .email("juan@example.com")
                .password("1234")
                .baseSalary(2000L)
                .birthDate(LocalDate.parse("1990-01-01"))
                .phone("3001234567")
                .document("100200300")
                .address("Calle 123")
                .build();

        clientRol = Rol.builder()
                .id(UUID.randomUUID())
                .name(Roles.CLIENT.getValue())
                .description("Cliente")
                .build();
    }

    // -------------------- findByEmail --------------------

    @Test
    void findUserByEmail_whenTheUserExist_thenShouldReturnIt() {
        // Arrange
        given(userRepository.findByEmail(user.getEmail())).willReturn(Mono.just(user));

        // Act & Assert
        StepVerifier.create(userUseCase.findByEmail(user.getEmail()))
                .expectNext(user)
                .verifyComplete();
    }

    // -------------------- save --------------------

    @Test
    void save_whenUserAlreadyExists_thenShouldThrowBusinessException() {
        // Arrange
        given(userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument()))
                .willReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(userUseCase.save(user, Roles.CLIENT.getValue()))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getCode().equals(BusinessErrorCode.USER_ALREADY_REGISTERED.getBusinessCode())
                )
                .verify();
    }

    @Test
    void save_whenUserDoesNotExistAndRolExists_thenShouldCreateIt() {
        // Arrange
        given(userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument()))
                .willReturn(Mono.just(false));
        given(rolRepository.findByName(Roles.CLIENT.getValue())).willReturn(Mono.just(clientRol));
        given(passwordCryptoGateway.encode(user.getPassword())).willReturn("encodedPassword");
        given(userRepository.save(user.toBuilder()
                .idRol(clientRol.getId())
                .password("encodedPassword")
                .build()))
                .willReturn(Mono.just(user));

        // Act & Assert
        StepVerifier.create(userUseCase.save(user, Roles.CLIENT.getValue()))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void save_whenRolDoesNotExist_thenShouldThrowBusinessException() {
        // Arrange
        given(userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument()))
                .willReturn(Mono.just(false));
        given(rolRepository.findByName(Roles.CLIENT.getValue())).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.save(user, Roles.CLIENT.getValue()))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getCode().equals(BusinessErrorCode.ROL_NOT_EXIST.getBusinessCode())
                )
                .verify();
    }

    @Test
    void save_whenUnexpectedErrorOccurs_thenShouldPropagateIt() {
        // Arrange
        given(userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument()))
                .willReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(userUseCase.save(user, Roles.CLIENT.getValue()))
                .expectError(RuntimeException.class)
                .verify();
    }

    // -------------------- findAll --------------------

    @Test
    void findAll_whenUsersExists_thenShouldReturnTextEventStream() {
        // Arrange
        given(userRepository.findAll()).willReturn(Flux.just(user, user));

        // Act & Assert
        StepVerifier.create(userUseCase.findAll())
                .expectNext(user)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void findAll_whenUnexpectedErrorOccurs_thenShouldPropagateIt() {
        // Arrange
        given(userRepository.findAll()).willReturn(Flux.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(userUseCase.findAll())
                .expectError(RuntimeException.class)
                .verify();
    }

    // -------------------- existsByDocumentAndEmail --------------------

    @Test
    void existsByDocumentAndEmail_ShouldReturnTrue_whenUserExists() {
        // Arrange
        given(userRepository.existsByDocumentAndEmail(user.getDocument(), user.getEmail()))
                .willReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(userUseCase.existsByDocumentAndEmail(user.getDocument(), user.getEmail()))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByDocumentAndEmail_ShouldReturnFalse_whenUserDoesNotExist() {
        // Arrange
        given(userRepository.existsByDocumentAndEmail(user.getDocument(), user.getEmail()))
                .willReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(userUseCase.existsByDocumentAndEmail(user.getDocument(), user.getEmail()))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByDocumentAndEmail_whenUnexpectedErrorOccurs_thenShouldPropagateIt() {
        // Arrange
        given(userRepository.existsByDocumentAndEmail(user.getDocument(), user.getEmail()))
                .willReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(userUseCase.existsByDocumentAndEmail(user.getDocument(), user.getEmail()))
                .expectError(RuntimeException.class)
                .verify();
    }

    // -------------------- validateCredentials --------------------

    @Test
    void validateCredentials_whenUserExistsAndPasswordMatches_thenShouldReturnUser() {
        // Arrange
        given(userRepository.findByEmail(user.getEmail())).willReturn(Mono.just(user));
        given(passwordCryptoGateway.matches(user.getPassword(), "1234")).willReturn(true);

        // Act & Assert
        StepVerifier.create(userUseCase.validateCredentials(user.getEmail(), "1234"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void validateCredentials_whenUserDoesNotExist_thenShouldThrowBusinessException() {
        // Arrange
        given(userRepository.findByEmail(user.getEmail())).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userUseCase.validateCredentials(user.getEmail(), "wrongpass"))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getCode().equals(BusinessErrorCode.USER_NOT_FOUND.getBusinessCode())
                )
                .verify();
    }

    @Test
    void validateCredentials_whenPasswordDoesNotMatch_thenShouldThrowBusinessException() {
        // Arrange
        given(userRepository.findByEmail(user.getEmail())).willReturn(Mono.just(user));
        given(passwordCryptoGateway.matches(user.getPassword(), "wrongpass")).willReturn(false);

        // Act & Assert
        StepVerifier.create(userUseCase.validateCredentials(user.getEmail(), "wrongpass"))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getCode().equals(BusinessErrorCode.INVALID_CREDENTIALS.getBusinessCode())
                )
                .verify();
    }

}
