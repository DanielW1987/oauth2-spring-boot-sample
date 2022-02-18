package rocks.danielw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(initializers = {KeyPairInitializer.class})
@TestPropertySource(locations = {"classpath:integrationtest.properties"})
class AuthorizationServerContextLoadTest {

	@Test
	@DisplayName("should load context with given key pair")
	void contextLoads() {
	}
}
