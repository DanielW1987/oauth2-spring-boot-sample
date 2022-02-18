package rocks.danielw.web;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class DemoRestControllerTest implements WithAssertions {

    private static final Jwt CLIENT1_JWT = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("scope", "endpoint-one")
        .build();

    private static final Jwt CLIENT2_JWT = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("scope", "endpoint-two")
        .build();

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Client1Tests {

        @Test
        @DisplayName("client1 should be allowed to access /api/v1/endpoint-one")
        void client_1_should_be_allowed_to_access_endpoint_one() throws Exception {
            // given
            doReturn(CLIENT1_JWT).when(jwtDecoder).decode(any());

            // when
            var result = mockMvc.perform(
                get("/api/v1/endpoint-one").header(AUTHORIZATION, String.format("Bearer %s", CLIENT1_JWT.getTokenValue()))
            ).andReturn();

            // then
            assertThat(result.getResponse().getStatus()).isEqualTo(OK.value());
        }

        @Test
        @DisplayName("client1 should not be allowed to access /api/v1/endpoint-two")
        void client_1_should_not_be_allowed_to_access_endpoint_two() throws Exception {
            // given
            doReturn(CLIENT1_JWT).when(jwtDecoder).decode(any());

            // when
            var result = mockMvc.perform(
                get("/api/v1/endpoint-two").header(AUTHORIZATION, String.format("Bearer %s", CLIENT1_JWT.getTokenValue()))
            ).andReturn();

            // then
            assertThat(result.getResponse().getStatus()).isEqualTo(FORBIDDEN.value());
        }
    }

    @Nested
    class Client2Tests {

        @Test
        @DisplayName("client2 should not be allowed to access /api/v1/endpoint-one")
        void client_2_should_not_be_allowed_to_access_endpoint_one() throws Exception {
            // given
            doReturn(CLIENT2_JWT).when(jwtDecoder).decode(any());

            // when
            var result = mockMvc.perform(
                get("/api/v1/endpoint-one").header(AUTHORIZATION, String.format("Bearer %s", CLIENT2_JWT.getTokenValue()))
            ).andReturn();

            // then
            assertThat(result.getResponse().getStatus()).isEqualTo(FORBIDDEN.value());
        }

        @Test
        @DisplayName("client2 should be allowed to access /api/v1/endpoint-two")
        void client_2_should_be_allowed_to_access_endpoint_two() throws Exception {
            // given
            doReturn(CLIENT2_JWT).when(jwtDecoder).decode(any());

            // when
            var result = mockMvc.perform(
                get("/api/v1/endpoint-two").header(AUTHORIZATION, String.format("Bearer %s", CLIENT2_JWT.getTokenValue()))
            ).andReturn();

            // then
            assertThat(result.getResponse().getStatus()).isEqualTo(OK.value());
        }
    }
}
