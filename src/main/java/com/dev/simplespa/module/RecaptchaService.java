package com.dev.simplespa.module;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecaptchaService {

	private final RestTemplate restTemplate;

	@Value("${recaptcha.url}")
	private String RECAPTCHA_VERIFY_URL;

	@Value("${recaptcha.secret}")
	private String secret;

	public boolean verify(String token) {
		String url = RECAPTCHA_VERIFY_URL + "?secret=" + secret + "&response=" + token;

		Map<String, Object> googleResponse = restTemplate.postForObject(url, null, Map.class);
		if (googleResponse == null || googleResponse.get("success") == null) {
			return false;
		}
		return (Boolean) googleResponse.get("success");
	}
}
