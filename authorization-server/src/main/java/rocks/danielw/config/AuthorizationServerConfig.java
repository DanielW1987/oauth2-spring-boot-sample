package rocks.danielw.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Import(OAuth2AuthorizationServerConfiguration.class)
public class AuthorizationServerConfig {

    private static final String KEY_ID = "auth-demo";

    @Bean
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        return httpSecurity.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
        return new JdbcRegisteredClientRepository(jdbcOperations);
    }

    @Bean
    public ProviderSettings providerSettings(@Value("${security.issuer-uri}") String issuerUri) {
        return ProviderSettings.builder()
            .issuer(issuerUri)
            .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(
        @Value("${security.authorization-server-private-key}") String privateKey,
        @Value("${security.authorization-server-public-key}") String publicKey) {
        var rsaKey = !privateKey.isBlank() && !publicKey.isBlank() ?
            loadRsa(privateKey, publicKey) :
            generateRsa();

        var jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    @SneakyThrows
    private RSAKey loadRsa(String privateKeyAsString, String publicKeyAsString) {
        var keyFactory = KeyFactory.getInstance("RSA");
        var keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyAsString));
        var privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpecPKCS8);

        var keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyAsString));
        var publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpecX509);

        return new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(KEY_ID)
            .build();
    }

    private RSAKey generateRsa() {
        var keyPair = generateRsaKey();
        var publicKey = (RSAPublicKey) keyPair.getPublic();
        var privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(KEY_ID)
            .build();
    }

    @SneakyThrows
    private KeyPair generateRsaKey() {
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
