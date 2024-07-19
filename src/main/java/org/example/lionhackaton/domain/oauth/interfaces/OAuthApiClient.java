package org.example.lionhackaton.domain.oauth.interfaces;

import org.example.lionhackaton.domain.oauth.OAuthProvider;

public interface OAuthApiClient {
	OAuthProvider oAuthProvider();
	String requestAccessToken(OAuthLoginParams params);
	OAuthInfoResponse requestOauthInfo(String accessToken);
}