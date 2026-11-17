package com.hotel.hotel_management.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.hotel_management.config.AuthCookieProperties;
import com.hotel.hotel_management.config.JwtConfiguration;
import com.hotel.hotel_management.config.JwtProperties;
import com.hotel.hotel_management.config.SecurityConfiguration;
import com.hotel.hotel_management.user.AccountStatus;
import com.hotel.hotel_management.user.Administrator;
import com.hotel.hotel_management.user.Receptionist;
import com.hotel.hotel_management.user.User;
import com.hotel.hotel_management.user.UserRepository;

import jakarta.servlet.http.Cookie;

/**
 * Exercises the login endpoint and the security filter chain through MockMvc, without a database:
 * only the user repository is replaced by a mock. CSRF is handled the way the frontend does it:
 * the token is requested from /api/auth/csrf and sent back in a cookie and a header.
 */
@WebMvcTest
@EnableConfigurationProperties({ JwtProperties.class, AuthCookieProperties.class })
@Import({
        SecurityConfiguration.class,
        JwtConfiguration.class,
        TokenService.class,
        AccessTokenCookieFactory.class,
        DatabaseUserDetailsService.class,
        AuthenticationIntegrationTest.ProbeController.class
})
class AuthenticationIntegrationTest {

    private static final String GENERIC_ERROR = "Invalid username or password";
    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private static final String XSRF_COOKIE = "XSRF-TOKEN";
    private static final String XSRF_HEADER = "X-XSRF-TOKEN";

    /** A protected endpoint that only exists in tests, to observe the authenticated principal. */
    @RestController
    static class ProbeController {

        @GetMapping("/api/probe")
        String probe(Authentication authentication) {
            return authentication.getName() + ":" + authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .sorted()
                    .collect(Collectors.joining(","));
        }

    }

    @DynamicPropertySource
    static void jwtAndCookieProperties(DynamicPropertyRegistry registry) {
        registry.add("app.jwt.secret", () -> Base64.getEncoder().encodeToString(new byte[32]));
        registry.add("app.jwt.expiration", () -> "PT1H");
        registry.add("app.jwt.issuer", () -> "hotel-management");
        registry.add("app.auth.cookie.name", () -> ACCESS_TOKEN_COOKIE);
        registry.add("app.auth.cookie.secure", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    private User receptionist(AccountStatus status) {
        User user = new Receptionist();
        user.setUsername("reception1");
        user.setPasswordHash(passwordEncoder.encode("Secret123"));
        user.setAccountStatus(status);
        user.setMustChangePassword(true);
        return user;
    }

    private String tokenFor(User user) {
        return tokenService.issue(user).value();
    }

    /** Requests a CSRF token from the API, as the frontend does before logging in. */
    private String requestXsrfToken() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isNoContent())
                .andReturn();
        String prefix = XSRF_COOKIE + "=";
        String header = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE).stream()
                .filter(value -> value.startsWith(prefix))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No " + XSRF_COOKIE + " cookie was set"));
        return header.split(";")[0].substring(prefix.length());
    }

    private MockHttpServletRequestBuilder loginRequest(String username, String password, boolean withCsrf)
            throws Exception {
        MockHttpServletRequestBuilder request = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
        if (withCsrf) {
            String token = requestXsrfToken();
            request.cookie(new Cookie(XSRF_COOKIE, token)).header(XSRF_HEADER, token);
        }
        return request;
    }

    private MockHttpServletRequestBuilder logoutRequest(boolean withCsrf) throws Exception {
        MockHttpServletRequestBuilder request = post("/api/auth/logout");
        if (withCsrf) {
                String token = requestXsrfToken();
                request.cookie(new Cookie(XSRF_COOKIE, token)).header(XSRF_HEADER, token);
        }
        return request;
    }

    // ---- CSRF endpoint ----

    @Test
    void csrfEndpointIsPublicAndSetsAScriptReadableCookie() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isNoContent())
                .andReturn();

        String setCookie = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE).stream()
                .filter(value -> value.startsWith(XSRF_COOKIE + "="))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No " + XSRF_COOKIE + " cookie was set"));
        assertThat(setCookie).doesNotContain("HttpOnly");
    }

    // ---- Login: successful ----

    @Test
    void validLoginReturnsUserDataAndSetsAHardenedCookie() throws Exception {
        when(userRepository.findByUsername("reception1"))
                .thenReturn(Optional.of(receptionist(AccountStatus.ACTIVE)));

        MvcResult result = mockMvc.perform(loginRequest("reception1", "Secret123", true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("reception1"))
                .andExpect(jsonPath("$.role").value("RECEPTIONIST"))
                .andExpect(jsonPath("$.mustChangePassword").value(true))
                .andReturn();

        String setCookie = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE).stream()
                .filter(value -> value.startsWith(ACCESS_TOKEN_COOKIE + "="))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No " + ACCESS_TOKEN_COOKIE + " cookie was set"));
        assertThat(setCookie)
                .contains("HttpOnly")
                .contains("SameSite=Strict")
                .contains("Path=/api")
                .doesNotContain("Secure");

        String tokenValue = setCookie.split(";")[0].substring((ACCESS_TOKEN_COOKIE + "=").length());
        assertThat(tokenValue).isNotBlank();
        assertThat(result.getResponse().getContentAsString()).doesNotContain(tokenValue);
    }

    // ---- Login: every failure looks the same ----

    @Test
    void wrongPasswordReturnsTheGenericError() throws Exception {
        when(userRepository.findByUsername("reception1"))
                .thenReturn(Optional.of(receptionist(AccountStatus.ACTIVE)));

        mockMvc.perform(loginRequest("reception1", "WrongPass1", true))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value(GENERIC_ERROR))
                .andExpect(cookie().doesNotExist(ACCESS_TOKEN_COOKIE));
    }

    @Test
    void unknownUserReturnsTheSameErrorAsAWrongPassword() throws Exception {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        mockMvc.perform(loginRequest("ghost", "Secret123", true))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value(GENERIC_ERROR))
                .andExpect(cookie().doesNotExist(ACCESS_TOKEN_COOKIE));
    }

    @Test
    void inactiveAccountReturnsTheSameErrorEvenWithTheRightPassword() throws Exception {
        when(userRepository.findByUsername("reception1"))
                .thenReturn(Optional.of(receptionist(AccountStatus.INACTIVE)));

        mockMvc.perform(loginRequest("reception1", "Secret123", true))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value(GENERIC_ERROR))
                .andExpect(cookie().doesNotExist(ACCESS_TOKEN_COOKIE));
    }

    // ---- Login: request validation ----

    @Test
    void loginWithoutACsrfTokenIsForbiddenAndDoesNotTouchTheDatabase() throws Exception {
        mockMvc.perform(loginRequest("reception1", "Secret123", false))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userRepository);
    }

    @Test
    void loginWithBlankFieldsIsABadRequest() throws Exception {
        mockMvc.perform(loginRequest("", "", true))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userRepository);
    }

    // ---- Token cookie handling in the filter chain ----

    @Test
    void protectedEndpointWithoutACookieIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/probe"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validTokenInTheCookieAuthenticatesWithTheRoleFromTheToken() throws Exception {
        User admin = new Administrator();
        admin.setUsername("boss");

        mockMvc.perform(get("/api/probe").cookie(new Cookie(ACCESS_TOKEN_COOKIE, tokenFor(admin))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("boss:")))
                .andExpect(content().string(containsString("ROLE_ADMINISTRATOR")))
                .andExpect(content().string(not(containsString("ROLE_RECEPTIONIST"))));
    }

    @Test
    void tamperedTokenInTheCookieIsUnauthorized() throws Exception {
        User admin = new Administrator();
        admin.setUsername("boss");
        String token = tokenFor(admin);
        String tampered = token.substring(0, token.length() - 4) + "AAAA";

        mockMvc.perform(get("/api/probe").cookie(new Cookie(ACCESS_TOKEN_COOKIE, tampered)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenSentInTheAuthorizationHeaderIsIgnored() throws Exception {
        User admin = new Administrator();
        admin.setUsername("boss");

        mockMvc.perform(get("/api/probe").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFor(admin)))
                .andExpect(status().isUnauthorized());
    }

    // ---- Logout ----

    @Test
    void logoutClearsTheAccessTokenCookie() throws Exception {
        User admin = new Administrator();
        admin.setUsername("boss");

        MvcResult result = mockMvc.perform(logoutRequest(true)
                        .cookie(new Cookie(ACCESS_TOKEN_COOKIE, tokenFor(admin))))
                        .andExpect(status().isNoContent())
                        .andReturn();
        
        String setCookie = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE).stream()
                .filter(value -> value.startsWith(ACCESS_TOKEN_COOKIE + "="))
                .findFirst()
                .orElseThrow(() -> new AssertionError("The access token cookie was not cleared"));

        assertThat(setCookie)
                .startsWith(ACCESS_TOKEN_COOKIE + "=;")
                .contains("Max-Age=0")
                .contains("Path=/api")
                .contains("HttpOnly");
    }

    @Test 
    void logoutWorksWithoutAValidSession() throws Exception {
        mockMvc.perform(logoutRequest(true))
                .andExpect(status().isNoContent());
    }

    @Test
    void logoutWithoutACsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(logoutRequest(false))
                .andExpect(status().isForbidden());
    }

}
