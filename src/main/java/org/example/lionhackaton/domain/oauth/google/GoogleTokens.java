package org.example.lionhackaton.domain.oauth.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleTokens {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("expires_in")
	private String expiresIn;

	@JsonProperty("scope")
	private String scope;

	@JsonProperty("id_token")
	private String idToken;
}
