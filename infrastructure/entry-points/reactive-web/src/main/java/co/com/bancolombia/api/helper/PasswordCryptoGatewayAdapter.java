package co.com.bancolombia.api.helper;

import co.com.bancolombia.model.user.gateways.PasswordCryptoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PasswordCryptoGatewayAdapter implements PasswordCryptoGateway {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public Boolean matches(String encodedPassword, String rawPassword) {
        return passwordEncoder.matches(rawPassword,encodedPassword);
    }
}
