package com.dev.simplespa.module;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

class RecaptchaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RecaptchaService recaptchaService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Inject secret and URL values
        ReflectionTestUtils.setField(recaptchaService, "secret", "test-secret");
        ReflectionTestUtils.setField(recaptchaService, "RECAPTCHA_VERIFY_URL", "https://fake-recaptcha.com/verify");
    }

    @Test
    void shouldReturnTrue_whenGoogleResponseSuccessIsTrue() {
        // given
        Map<String, Object> mockResponse = Map.of("success", true);

        when(restTemplate.postForObject(
            anyString(),
            isNull(),
            eq(Map.class)
        )).thenReturn(mockResponse);

        // when
        boolean result = recaptchaService.verify("valid-token");

        // then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_whenGoogleResponseSuccessIsFalse() {
        // given
        Map<String, Object> mockResponse = Map.of("success", false);

        when(restTemplate.postForObject(
            anyString(),
            isNull(),
            eq(Map.class)
        )).thenReturn(mockResponse);

        // when
        boolean result = recaptchaService.verify("invalid-token");

        // then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalse_whenGoogleResponseIsNull() {
        when(restTemplate.postForObject(anyString(), isNull(), eq(Map.class)))
            .thenReturn(null);

        boolean result = recaptchaService.verify("token");

        assertFalse(result);
    }


}
