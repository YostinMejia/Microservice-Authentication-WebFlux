package co.com.bancolombia.api;

import co.com.bancolombia.api.auth.AuthHandler;
import co.com.bancolombia.api.auth.config.AuthPath;
import co.com.bancolombia.api.auth.dto.LoginDto;
import co.com.bancolombia.api.auth.dto.SameEmailAsTokenDto;
import co.com.bancolombia.api.dto.ResponseDto;
import co.com.bancolombia.api.helper.RequestValidator;
import co.com.bancolombia.api.user.UserHandler;
import co.com.bancolombia.api.user.config.UserPath;
import co.com.bancolombia.api.user.dto.CreateUserDto;
import co.com.bancolombia.api.user.dto.ExistsByDocumentAndEmailDto;
import co.com.bancolombia.api.user.dto.UserDataResponseDto;
import co.com.bancolombia.api.user.mapper.UserDtoMapper;
import co.com.bancolombia.model.auth.gateways.AuthRepository;
import co.com.bancolombia.model.dto.MultipleErrorsResponseDto;
import co.com.bancolombia.model.dto.SingleErrorResponseDto;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.PasswordCryptoGateway;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.model.utils.BusinessErrorCode;
import co.com.bancolombia.model.utils.ResponseCode;
import co.com.bancolombia.usecase.auth.AuthUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ContextConfiguration(classes = {
        RouterRest.class,
        UserHandler.class,
        UserPath.class,
        UserUseCase.class,
        AuthHandler.class,
        AuthUseCase.class,
        AuthPath.class,
        GlobalErrorWebExceptionHandler.class
})
@WebFluxTest(
        controllers = UserHandler.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        }
)
@TestPropertySource(properties = {
        "routes.paths.users.users=/api/v1/ususario",
        "routes.paths.users.findByEmail=/api/v1/usuarios/{email}",
        "routes.paths.users.existsByDocumentAndEmail=/api/v1/usuarios/exists",
        "routes.paths.users.findRoleNameByEmail=/api/v1/usuarios/{email}/exists",
        "routes.paths.auth.login=/api/v1/login",
        "routes.paths.auth.isSameEmailAsToken=/api/v1/auth/same-email"
})
public class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserPath userPath;

    @Autowired
    private GlobalErrorWebExceptionHandler globalErrorWebExceptionHandler;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    @MockitoBean
    private RolRepository rolRepository;

    @MockitoBean
    private PasswordCryptoGateway passwordCryptoGateway;

    @MockitoBean
    private AuthUseCase authUseCase;

    @MockitoBean
    private AuthRepository authRepository;

    @MockitoBean
    private RequestValidator requestValidator;

    private CreateUserDto createUserDto;
    private User user;
    private Rol clientRol;

    @BeforeEach
    void setUp() {
        clientRol = new Rol().toBuilder()
                .id(UUID.randomUUID())
                .description("Rol de cliente")
                .name("cliente")
                .build();

        createUserDto = new CreateUserDto(
                "Juan",
                "Perez",
                "test@mail.com",
                "pass123",
                clientRol.getName(),
                1_000_000L,
                "2000-01-01",
                "+57 3001234567",
                "123456",
                "Calle 1"
        );

        user = User.builder()
                .name(createUserDto.name())
                .lastName(createUserDto.lastName())
                .email(createUserDto.email())
                .password(createUserDto.password())
                .baseSalary(createUserDto.baseSalary())
                .birthDate(LocalDate.parse(createUserDto.birthDate()))
                .phone(createUserDto.phone())
                .document(createUserDto.document())
                .address(createUserDto.address())
                .build();
    }


    @Test
    void listenFindRoleNameByEmail_ShouldReturnRoleName_WhenUserExists() {
        String roleName = "CLIENT";

        given(userUseCase.findRoleNameByEmail(user.getEmail())).willReturn(Mono.just(roleName));

        ResponseDto<String> responseExpected = new ResponseDto<>(
                ResponseCode.USER_ROLE_FOUND.getMessage(),
                ResponseCode.USER_ROLE_FOUND.getBusinessCode(),
                roleName
        );

        webTestClient.get()
                .uri(userPath.getFindRoleNameByEmail(), user.getEmail())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .consumeWith(response -> {
                    ResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                    assertThat(body.data()).isEqualTo(responseExpected.data());
                });
    }

    @Test
    void listenFindRoleNameByEmail_ShouldReturnError_WhenUserDoesNotExist() {
        given(userUseCase.findRoleNameByEmail(user.getEmail()))
                .willReturn(Mono.error(new BusinessException(BusinessErrorCode.USER_NOT_FOUND)));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.USER_NOT_FOUND.getMessage(),
                BusinessErrorCode.USER_NOT_FOUND.getBusinessCode()
        );

        webTestClient.get()
                .uri(userPath.getFindRoleNameByEmail(), user.getEmail())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }

    @Test
    void userListenSave_ShouldCreateUser_whenUserDoesNotExistsAndIsAuthorized() {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto(
                createUserDto.name(),
                createUserDto.lastName(),
                createUserDto.email(),
                clientRol.getId(),
                createUserDto.baseSalary(),
                createUserDto.birthDate(),
                createUserDto.phone(),
                createUserDto.document(),
                createUserDto.address()
        );
        User userWithId = user.toBuilder()
                .id(UUID.randomUUID())
                .build();

        given(requestValidator.validator(createUserDto)).willReturn(Mono.just(createUserDto));
        given(userDtoMapper.toUser(createUserDto)).willReturn(user);
        given(userUseCase.save(user, clientRol.getName())).willReturn(Mono.just(userWithId));
        given(userDtoMapper.toUserResponseDto(userWithId)).willReturn(userDataResponseDto);

        ResponseDto<UserDataResponseDto> responseExpected = new ResponseDto<>(
                ResponseCode.USER_CREATED.getMessage(),
                ResponseCode.USER_CREATED.getBusinessCode(),
                userDataResponseDto
        );

        webTestClient.post()
                .uri(userPath.getUsers())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseDto.class)
                .consumeWith(response -> {
                    ResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());

                    Map<?, ?> data = (Map<?, ?>) body.data();
                    assertThat(data.get("email")).isEqualTo(responseExpected.data().email());
                    assertThat(data.get("document")).isEqualTo(responseExpected.data().document());
                });
    }

    @Test
    void userListenSave_ShouldNotCreateUser_whenEmailOrDocumentExists() {
        given(requestValidator.validator(createUserDto)).willReturn(Mono.just(createUserDto));
        given(userDtoMapper.toUser(createUserDto)).willReturn(user);
        given(userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument()))
                .willReturn(Mono.just(true));
        given(userUseCase.save(user, clientRol.getName()))
                .willReturn(Mono.error(new BusinessException(BusinessErrorCode.USER_ALREADY_REGISTERED)));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(BusinessErrorCode.USER_ALREADY_REGISTERED.getMessage(), BusinessErrorCode.USER_ALREADY_REGISTERED.getBusinessCode());

        webTestClient.post()
                .uri(userPath.getUsers())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto responseBody = response.getResponseBody();
                    assertThat(responseBody.message()).isEqualTo(responseExpected.message());
                    assertThat(responseBody.code()).isEqualTo(responseExpected.code());
                });

    }

    @Test
    void userListenSave_ShouldReturnBadRequest_whenRequestBodyInvalid() {
        List<String> errors = List.of("password: no debe estar vacío","password: no debe ser nulo","email: no debe ser nulo");

        given(requestValidator.validator(createUserDto))
                .willReturn(Mono.error(new BusinessException(errors, BusinessErrorCode.VALIDATION_FAILED)));

        MultipleErrorsResponseDto responseExpected = new MultipleErrorsResponseDto(
                errors,
                BusinessErrorCode.VALIDATION_FAILED.getMessage(),
                BusinessErrorCode.VALIDATION_FAILED.getBusinessCode()
        );

        webTestClient.post()
                .uri(userPath.getUsers())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(MultipleErrorsResponseDto.class)
                .consumeWith(response -> {
                    MultipleErrorsResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    Assertions.assertNotNull(body);
                    assertThat(body.errors().size()).isEqualTo(3);
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }

    @Test
    void userListenSave_ShouldReturnInternalError_whenAnUnknownErrorOccur() {
        given(requestValidator.validator(createUserDto)).willReturn(Mono.just(createUserDto));
        given(userDtoMapper.toUser(createUserDto)).willReturn(user);
        given(userUseCase.save(user, clientRol.getName()))
                .willReturn(Mono.error(new RuntimeException("Unexpected error")));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getBusinessCode()
        );

        webTestClient.post()
                .uri(userPath.getUsers())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }


    @Test
    void userListenExistsByDocumentAndEmail_ShouldReturnTrue_whenUserExists() {
        ExistsByDocumentAndEmailDto requestDto = new ExistsByDocumentAndEmailDto("123456789", "john.doe@mail.com");

        given(userUseCase.existsByDocumentAndEmail(requestDto.document(), requestDto.email()))
                .willReturn(Mono.just(true));

        ResponseDto<Boolean> responseExpected = new ResponseDto<>(
                ResponseCode.USER_EXISTS.getMessage(),
                ResponseCode.USER_EXISTS.getBusinessCode(),
                true
        );

        webTestClient.post()
                .uri(userPath.getExistsByDocumentAndEmail())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .consumeWith(response -> {
                    ResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                    assertThat(body.data()).isEqualTo(responseExpected.data());
                });
    }

    @Test
    void userListenExistsByDocumentAndEmail_ShouldReturnFalse_whenUserDoesNotExists() {
        ExistsByDocumentAndEmailDto requestDto = new ExistsByDocumentAndEmailDto("123456789", "john.doe@mail.com");

        given(userUseCase.existsByDocumentAndEmail(requestDto.document(), requestDto.email()))
                .willReturn(Mono.just(false));

        ResponseDto<Boolean> responseExpected = new ResponseDto<>(
                BusinessErrorCode.USER_NOT_FOUND.getMessage(),
                BusinessErrorCode.USER_NOT_FOUND.getBusinessCode(),
                false
        );

        webTestClient.post()
                .uri(userPath.getExistsByDocumentAndEmail())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .consumeWith(response -> {
                    ResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                    assertThat(body.data()).isEqualTo(responseExpected.data());
                });
    }

    @Test
    void userListenExistsByDocumentAndEmail_ShouldReturnInternalError_whenAnUnknownErrorOccur() {
        ExistsByDocumentAndEmailDto requestDto = new ExistsByDocumentAndEmailDto("123456789", "john.doe@mail.com");

        given(userUseCase.existsByDocumentAndEmail(requestDto.document(), requestDto.email()))
                .willReturn(Mono.error(new RuntimeException("Unexpected error")));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getBusinessCode()
        );

        webTestClient.post()
                .uri(userPath.getExistsByDocumentAndEmail())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }

    @Test
    void userListenGetAll_ShouldReturnTextEventStream_whenUsersExists() {
        User userWithId = user.toBuilder().id(UUID.randomUUID()).build();
        User anotherUser = user.toBuilder()
                .id(UUID.randomUUID())
                .email("another@mail.com")
                .document("654321")
                .build();

        UserDataResponseDto dto1 = new UserDataResponseDto(
                userWithId.getName(),
                userWithId.getLastName(),
                userWithId.getEmail(),
                UUID.randomUUID(),
                userWithId.getBaseSalary(),
                userWithId.getBirthDate().toString(),
                userWithId.getPhone(),
                userWithId.getDocument(),
                userWithId.getAddress()
        );

        UserDataResponseDto dto2 = new UserDataResponseDto(
                anotherUser.getName(),
                anotherUser.getLastName(),
                anotherUser.getEmail(),
                UUID.randomUUID(),
                anotherUser.getBaseSalary(),
                anotherUser.getBirthDate().toString(),
                anotherUser.getPhone(),
                anotherUser.getDocument(),
                anotherUser.getAddress()
        );

        given(userUseCase.findAll()).willReturn(Flux.just(userWithId, anotherUser));
        given(userDtoMapper.toUserResponseDto(userWithId)).willReturn(dto1);
        given(userDtoMapper.toUserResponseDto(anotherUser)).willReturn(dto2);

        webTestClient.get()
                .uri(userPath.getUsers())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBodyList(UserDataResponseDto.class)
                .hasSize(2)
                .contains(dto1, dto2);
    }

    @Test
    void userListenGetAll_ShouldReturnInternalError_whenAnUnknownErrorOccurs() {
        given(userUseCase.findAll())
                .willReturn(Flux.error(new RuntimeException("Unexpected error")));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getBusinessCode()
        );

        webTestClient.get()
                .uri(userPath.getUsers())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }

    @Test
    void authListenLogin_ShouldReturnToken_whenCredentialsAreValid() {
        LoginDto loginDto = new LoginDto("test@mail.com", "password123");
        String token = "jwt-token";

        given(requestValidator.validator(loginDto)).willReturn(Mono.just(loginDto));
        given(authUseCase.login(loginDto.email(), loginDto.password())).willReturn(Mono.just(token));

        ResponseDto<String> responseExpected = new ResponseDto<>(
                ResponseCode.TOKEN_GENERATED.getMessage(),
                ResponseCode.TOKEN_GENERATED.getBusinessCode(),
                token
        );

        webTestClient.post()
                .uri("/api/v1/login")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .consumeWith(response -> {
                    ResponseDto body = response.getResponseBody();
                    assertThat(body).isNotNull();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                    assertThat(body.data()).isEqualTo(responseExpected.data());
                });
    }

    @Test
    void authListenLogin_ShouldReturnBadRequest_whenRequestInvalid() {
        LoginDto loginDto = new LoginDto("bad-email", "pass");

        given(requestValidator.validator(loginDto))
                .willReturn(Mono.error(new BusinessException(BusinessErrorCode.VALIDATION_FAILED)));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.VALIDATION_FAILED.getMessage(),
                BusinessErrorCode.VALIDATION_FAILED.getBusinessCode()
        );

        webTestClient.post()
                .uri("/api/v1/login")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }



    @Test
    void authListenLogin_ShouldReturnInternalError_whenUnexpectedError() {
        LoginDto loginDto = new LoginDto("test@mail.com", "password123");

        given(requestValidator.validator(loginDto)).willReturn(Mono.just(loginDto));
        given(authUseCase.login(loginDto.email(), loginDto.password()))
                .willReturn(Mono.error(new RuntimeException("Unexpected error")));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getBusinessCode()
        );

        webTestClient.post()
                .uri("/api/v1/login")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }

    @Test
    void authIsSameEmailAsToken_ShouldReturnOk_whenEmailMatchesToken() {
        SameEmailAsTokenDto dto = new SameEmailAsTokenDto("test@mail.com");

        given(requestValidator.validator(dto)).willReturn(Mono.just(dto));
        given(authUseCase.isSameEmailAsToken(dto.email())).willReturn(Mono.just(true));

        ResponseDto<Boolean> responseExpected = new ResponseDto<>(
                ResponseCode.IS_SAME_EMAIL.getMessage(),
                ResponseCode.IS_SAME_EMAIL.getBusinessCode(),
                true
        );

        webTestClient.post()
                .uri("/api/v1/auth/same-email")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .consumeWith(response -> {
                    ResponseDto body = response.getResponseBody();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                    assertThat(body.data()).isEqualTo(responseExpected.data());
                });
    }

    @Test
    void authIsSameEmailAsToken_ShouldReturnBusinessError_whenEmailNotSame() {
        SameEmailAsTokenDto dto = new SameEmailAsTokenDto("wrong@mail.com");

        given(requestValidator.validator(dto)).willReturn(Mono.just(dto));
        given(authUseCase.isSameEmailAsToken(dto.email())).willReturn(Mono.just(false));

        ResponseDto<Boolean> responseExpected = new ResponseDto<>(
                BusinessErrorCode.IS_NOT_SAME_EMAIL.getMessage(),
                BusinessErrorCode.IS_NOT_SAME_EMAIL.getBusinessCode(),
                false
        );

        webTestClient.post()
                .uri("/api/v1/auth/same-email")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDto.class)
                .consumeWith(response -> {
                    ResponseDto body = response.getResponseBody();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                    assertThat(body.data()).isEqualTo(responseExpected.data());
                });
    }

    @Test
    void authIsSameEmailAsToken_ShouldReturnBadRequest_whenRequestInvalid() {
        SameEmailAsTokenDto dto = new SameEmailAsTokenDto("");

        given(requestValidator.validator(dto))
                .willReturn(Mono.error(new BusinessException(BusinessErrorCode.VALIDATION_FAILED)));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.VALIDATION_FAILED.getMessage(),
                BusinessErrorCode.VALIDATION_FAILED.getBusinessCode()
        );

        webTestClient.post()
                .uri("/api/v1/auth/same-email")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }

    @Test
    void authIsSameEmailAsToken_ShouldReturnInternalError_whenUnexpectedError() {
        SameEmailAsTokenDto dto = new SameEmailAsTokenDto("test@mail.com");

        given(requestValidator.validator(dto)).willReturn(Mono.just(dto));
        given(authUseCase.isSameEmailAsToken(dto.email()))
                .willReturn(Mono.error(new RuntimeException("Unexpected error")));

        SingleErrorResponseDto responseExpected = new SingleErrorResponseDto(
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                BusinessErrorCode.INTERNAL_SERVER_ERROR.getBusinessCode()
        );

        webTestClient.post()
                .uri("/api/v1/auth/same-email")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(SingleErrorResponseDto.class)
                .consumeWith(response -> {
                    SingleErrorResponseDto body = response.getResponseBody();
                    assertThat(body.message()).isEqualTo(responseExpected.message());
                    assertThat(body.code()).isEqualTo(responseExpected.code());
                });
    }



}
