package net.trendd.App.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FacebookTokenResponse(@JsonProperty("access_token") String accessToken,@JsonProperty("token_type") String tokenType, @JsonProperty("expires_in")String expiresIn) {
}
