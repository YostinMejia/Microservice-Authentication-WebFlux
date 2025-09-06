package co.com.bancolombia.api.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PasswordCryptoGatewayAdapterTest {

    @InjectMocks
    private PasswordCryptoGatewayAdapter passwordCryptoGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String rawPassword = "mySecret123";
    private final String encodedPassword = "$2a$10$somehashedvalue";

    @Test
    void encode_shouldReturnEncodedPassword() {
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);

        String result = passwordCryptoGateway.encode(rawPassword);

        assertThat(result).isEqualTo(encodedPassword);
    }

    @Test
    void matches_shouldReturnTrue_whenPasswordsMatch() {
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        Boolean result = passwordCryptoGateway.matches(encodedPassword, rawPassword);

        assertThat(result).isTrue();
    }

    @Test
    void matches_shouldReturnFalse_whenPasswordsDoNotMatch() {
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

        Boolean result = passwordCryptoGateway.matches(encodedPassword, rawPassword);

        assertThat(result).isFalse();
    }
}
