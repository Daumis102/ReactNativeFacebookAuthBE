package net.trendd.App.domain.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FacebookMeResponse(@JsonProperty("id") String id, @JsonProperty("first_name") String firstName,
                                 @JsonProperty("last_name") String lastName) {}
