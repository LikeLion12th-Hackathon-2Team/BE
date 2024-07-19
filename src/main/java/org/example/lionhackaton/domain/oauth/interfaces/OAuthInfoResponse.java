package org.example.lionhackaton.domain.oauth.interfaces;

import org.example.lionhackaton.domain.oauth.OAuthProvider;

public interface OAuthInfoResponse {
	String getEmail();
	String getNickname();
	OAuthProvider getOAuthProvider();
}