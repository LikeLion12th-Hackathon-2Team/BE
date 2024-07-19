package org.example.lionhackaton.domain.oauth.kakao;

import org.example.lionhackaton.domain.oauth.interfaces.OAuthInfoResponse;
import org.example.lionhackaton.domain.oauth.OAuthProvider;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoInfoResponse implements OAuthInfoResponse {

	@JsonProperty("kakao_account")
	private KakaoAccount kakaoAccount;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class KakaoAccount {
		private KakaoProfile profile;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class KakaoProfile {
		private String nickname;
	}

	@Override
	public String getEmail() {
		return null;
	}

	@Override
	public String getNickname() {
		return kakaoAccount.profile.nickname;
	}

	@Override
	public OAuthProvider getOAuthProvider() {
		return OAuthProvider.KAKAO;
	}
}