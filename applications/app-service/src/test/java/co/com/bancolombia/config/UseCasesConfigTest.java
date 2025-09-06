package co.com.bancolombia.config;

import co.com.bancolombia.model.auth.gateways.AuthRepository;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.model.user.gateways.PasswordCryptoGateway;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.usecase.auth.AuthUseCase;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            String[] beanNames = context.getBeanDefinitionNames();
            boolean useCaseBeanFound = false;

            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    Object bean = context.getBean(beanName);
                    assertNotNull(bean, "El bean " + beanName + " no debería ser null");
                    useCaseBeanFound = true;

                    // Ejemplo: validar tipos concretos
                    if (bean instanceof UserUseCase userUseCase) {
                        assertNotNull(userUseCase);
                    }
                    if (bean instanceof AuthUseCase authUseCase) {
                        assertNotNull(authUseCase);
                    }
                }
            }

            assertTrue(useCaseBeanFound, "No se encontraron beans que terminen en 'UseCase'");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class) // importa tu configuración real
    static class TestConfig {
        // Mockeamos todas las dependencias necesarias
        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public RolRepository rolRepository() {
            return Mockito.mock(RolRepository.class);
        }

        @Bean
        public PasswordCryptoGateway passwordCryptoGateway() {
            return Mockito.mock(PasswordCryptoGateway.class);
        }

        @Bean
        public AuthRepository authRepository() {
            return Mockito.mock(AuthRepository.class);
        }
    }
}
