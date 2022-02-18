package rocks.danielw.util;

import java.util.Set;

public record ClientProperties(
    String clientId,
    String clientSecret,
    Set<String> scopes
) {}
