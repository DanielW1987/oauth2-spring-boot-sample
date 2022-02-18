package rocks.danielw;

import com.nimbusds.jose.util.Base64;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyPairInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            var keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(2048);
            var keyPair = keyGenerator.generateKeyPair();
            var privateKey = Base64.encode(keyPair.getPrivate().getEncoded());
            var publicKey = Base64.encode(keyPair.getPublic().getEncoded());
            TestPropertyValues.of(
                String.format("security.authorization-server-private-key=%s", privateKey),
                String.format("security.authorization-server-public-key=%s", publicKey)
            ).applyTo(applicationContext.getEnvironment());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
