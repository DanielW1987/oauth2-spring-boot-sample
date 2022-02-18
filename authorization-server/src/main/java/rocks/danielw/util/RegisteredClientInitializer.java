package rocks.danielw.util;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

@Component
@AllArgsConstructor
public class RegisteredClientInitializer implements ApplicationRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final ClientConfigurationProperties clientConfigurationProperties;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Map.Entry<String, ClientProperties> entry: clientConfigurationProperties.entrySet()) {
            String clientName = entry.getKey();
            ClientProperties client = entry.getValue();
            if (registeredClientRepository.findByClientId(client.clientId()) == null) {
                RegisteredClient.Builder clientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId(client.clientId())
                    .clientSecret(bCryptPasswordEncoder.encode(client.clientSecret()))
                    .clientName(clientName)
                    .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
                    .authorizationGrantType(CLIENT_CREDENTIALS)
                    .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(1)).build());

                client.scopes().forEach(clientBuilder::scope);
                registeredClientRepository.save(clientBuilder.build());
            }
        }
    }
}
