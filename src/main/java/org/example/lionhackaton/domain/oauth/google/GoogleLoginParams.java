package org.example.lionhackaton.domain.oauth.google;

import org.example.lionhackaton.domain.oauth.OAuthProvider;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthLoginParams;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginParams implements OAuthLoginParams {
	private String authorizationCode;

	@Override
	public OAuthProvider oAuthProvider() {
		return OAuthProvider.GOOGLE;
	}

	@Override
	public MultiValueMap<String, String> makeBody() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("code", authorizationCode);
		return body;
	}
}
