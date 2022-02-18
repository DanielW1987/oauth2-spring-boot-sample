package rocks.danielw.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@ConfigurationProperties(prefix = "oauth-clients")
public class ClientConfigurationProperties extends HashMap<String, ClientProperties> {
}
