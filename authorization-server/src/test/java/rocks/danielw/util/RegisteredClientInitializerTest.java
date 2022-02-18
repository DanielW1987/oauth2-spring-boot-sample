package rocks.danielw.util;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.time.Duration;
import java.util.Set;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;

@ExtendWith(MockitoExtension.class)
class RegisteredClientInitializerTest implements WithAssertions {

    @Mock
    private RegisteredClientRepository registeredClientRepository;

    @Spy
    private ClientConfigurationProperties clientConfigurationProperties;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private RegisteredClientInitializer underTest;

    private ClientProperties client1;
    private ClientProperties client2;

    @BeforeEach
    void init() {
        client1 = new ClientProperties("client-1-id", "client-1-secret", Set.of("scope-1"));
        client2 = new ClientProperties("client-2-id", "client-2-secret", Set.of("scope-2"));
        clientConfigurationProperties.put("client1", client1);
        clientConfigurationProperties.put("client2", client2);
    }

    @Test
    @DisplayName("should search every client in repository")
    void should_search_every_client_in_repository_before_creating_a_client() {
        // when
        underTest.run(mock(ApplicationArguments.class));

        // then
        verify(registeredClientRepository).findByClientId("client-1-id");
        verify(registeredClientRepository).findByClientId("client-2-id");
    }



    @Test
    @DisplayName("should save every client that does not exist")
    void should_save_every_client_that_does_not_exist() {
        // given
        doReturn(mock(RegisteredClient.class)).when(registeredClientRepository).findByClientId("client-2-id");

        // when
        underTest.run(mock(ApplicationArguments.class));

        // then
        ArgumentCaptor<RegisteredClient> captor = ArgumentCaptor.forClass(RegisteredClient.class);
        verify(registeredClientRepository).save(captor.capture());
        assertThat(captor.getValue().getClientId()).isEqualTo("client-1-id");
    }

    @Test
    @DisplayName("should encrypt every client secret")
    void should_encrypt_every_client_secret() {
        // when
        underTest.run(mock(ApplicationArguments.class));

        // then
        verify(bCryptPasswordEncoder).encode(client1.clientSecret());
        verify(bCryptPasswordEncoder).encode(client2.clientSecret());
    }

    @Test
    @DisplayName("should create a client with certain properties")
    void should_create_a_client() {
        // given
        doReturn(mock(RegisteredClient.class)).when(registeredClientRepository).findByClientId("client-2-id");

        // when
        underTest.run(mock(ApplicationArguments.class));

        // then
        ArgumentCaptor<RegisteredClient> captor = ArgumentCaptor.forClass(RegisteredClient.class);
        verify(registeredClientRepository).save(captor.capture());
        assertThat(captor.getValue().getClientId()).isEqualTo("client-1-id");
        assertThat(captor.getValue().getClientName()).isEqualTo("client1");
        assertThat(captor.getValue().getClientAuthenticationMethods()).containsExactly(CLIENT_SECRET_BASIC);
        assertThat(captor.getValue().getAuthorizationGrantTypes()).containsExactly(CLIENT_CREDENTIALS);
        assertThat(captor.getValue().getTokenSettings().getAccessTokenTimeToLive()).isEqualTo(Duration.ofMinutes(1));
    }
}
