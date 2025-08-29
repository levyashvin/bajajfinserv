package com.example.bajajfinserv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookResponse(
    @JsonProperty("webhook") String webhook,
    @JsonProperty("accessToken") String accessToken
) {}
