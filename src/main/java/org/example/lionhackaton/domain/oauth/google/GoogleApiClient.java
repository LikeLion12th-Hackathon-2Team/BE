package org.example.lionhackaton.domain.oauth.google;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.example.lionhackaton.domain.oauth.OAuthProvider;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthApiClient;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthInfoResponse;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthLoginParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleApiClient implements OAuthApiClient {
	private static final String GRANT_TYPE = "authorization_code";

	@Value("${oauth.google.url.auth}")
	private String apiUrl;

	@Value("${oauth.google.url.api}")
	private String userInfoUrl;

	@Value("${oauth.google.url.redirect}")
	private String redirectUrl;

	@Value("${oauth.google.client-id}")
	private String clientId;

	@Value("${oauth.google.client-secret}")
	private String clientSecret;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public OAuthProvider oAuthProvider() {
		return OAuthProvider.GOOGLE;
	}

	@Override
	public String requestAccessToken(OAuthLoginParams params) {
		String url = apiUrl;

		String decoded = URLDecoder.decode(params.makeBody().get("code").get(0), StandardCharsets.UTF_8);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("code", decoded);
		body.add("grant_type", GRANT_TYPE);
		body.add("redirect_uri", redirectUrl);
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);

		HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);
		GoogleTokens response = restTemplate.postForObject(url, request, GoogleTokens.class);

		assert response != null;
		return response.getAccessToken();
	}

	@Override
	public OAuthInfoResponse requestOauthInfo(String accessToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);

			HttpEntity<Void> request = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);
			return objectMapper.readValue(response.getBody(), GoogleInfoResponse.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to retrieve user info from Google", e);
		}
	}
}
