package org.example.lionhackaton.domain.oauth.interfaces;

import org.example.lionhackaton.domain.oauth.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
	OAuthProvider oAuthProvider();
	MultiValueMap<String, String> makeBody();
}