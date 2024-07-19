package org.example.lionhackaton.domain.oauth.google;

import org.example.lionhackaton.domain.oauth.OAuthProvider;
import org.example.lionhackaton.domain.oauth.interfaces.OAuthInfoResponse;
import org.example.lionhackaton.domain.oauth.naver.NaverInfoResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleInfoResponse implements OAuthInfoResponse {
	private String id;
	private String email;
	private String verified_email;
	private String picture;
	private String hd;

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getNickname() {
		return email.split("@")[0];
	}

	@Override
	public OAuthProvider getOAuthProvider() {
		return OAuthProvider.GOOGLE;
	}
}
