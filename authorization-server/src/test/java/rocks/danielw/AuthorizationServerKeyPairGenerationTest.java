package rocks.danielw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
	locations = {"classpath:integrationtest.properties"},
	properties = {"security.authorization-server-private-key=", "security.authorization-server-public-key="}
)
class AuthorizationServerKeyPairGenerationTest {

	@Test
	@DisplayName("should load context if private key and public key are empty")
	void contextLoads() {
	}
}
