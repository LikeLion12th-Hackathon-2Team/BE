package org.example.lionhackaton.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.example.lionhackaton.domain.oauth.OAuthProvider;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthApiClient;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthInfoResponse;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthLoginParams;
import org.springframework.stereotype.Component;

@Component
public class RequestOAuthInfoService {
	private final Map<OAuthProvider, OAuthApiClient> clients;

	public RequestOAuthInfoService(List<OAuthApiClient> clients) {
		this.clients = clients.stream().collect(
			Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
		);
	}

	public OAuthInfoResponse request(OAuthLoginParams params) {
		OAuthApiClient client = clients.get(params.oAuthProvider());
		String accessToken = client.requestAccessToken(params);
		return client.requestOauthInfo(accessToken);
	}
}