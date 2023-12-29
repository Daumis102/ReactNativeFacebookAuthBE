package net.trendd.App.services;

import net.trendd.App.controllers.exceptions.FacebookInvalidTokenException;
import net.trendd.App.controllers.exceptions.FacebookOAuthException;
import net.trendd.App.domain.responses.FacebookMeResponse;
import net.trendd.App.domain.responses.FacebookTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class FacebookService {

    private final UriComponentsBuilder graphApiUrlBuilder;
    private final RestClient restClient;
    Logger logger = LoggerFactory.getLogger(FacebookService.class);
    @Value("${facebook.app.id}")
    private String facebookAppId;

    @Value("${facebook.client.secret}")
    private String facebookAccessToken;

    public FacebookService(RestClient restClient) {
        this.graphApiUrlBuilder = UriComponentsBuilder.fromHttpUrl("https://graph.facebook.com").pathSegment("v18.0");
        this.restClient = restClient;
    }

    public FacebookMeResponse getUserDetails(String tempAccessToken) {
        logger.info("Attempting to get Facebook user");
        String urlTemplate = graphApiUrlBuilder.cloneBuilder()
                                               .pathSegment("me")
                                               .queryParam("access_token", tempAccessToken)
                                               .queryParam("fields", "id,first_name,last_name")
                                               .encode()
                                               .toUriString();

        return restClient.get()
                         .uri(urlTemplate).accept(APPLICATION_JSON)
                         .retrieve()
                         .body(FacebookMeResponse.class);
    }

    public String getAccessToken(String tempAccessToken) throws FacebookInvalidTokenException {
        logger.info("Starting Facebook Auth for: %s".formatted(tempAccessToken));

        String url = graphApiUrlBuilder.cloneBuilder().pathSegment("oauth", "access_token")
                                       .queryParam("grant_type", "fb_exchange_token")
                                       .queryParam("fb_exchange_token", tempAccessToken)
                                       .queryParam("client_id", facebookAppId)
                                       .queryParam("client_secret", facebookAccessToken).encode().toUriString();


        FacebookTokenResponse facebookResponse = restClient.get()
                                                           .uri(url)
                                                           .retrieve()
                                                           .onStatus(HttpStatusCode::is5xxServerError,
                                                                     (request, response) -> {
                                                                         throw new FacebookOAuthException(response.getStatusCode(),
                                                                                                          response.getHeaders());
                                                                     })
                                                           .onStatus(HttpStatusCode::is4xxClientError,
                                                                     (request, response) -> {
                                                                         throw new FacebookOAuthException(response.getStatusCode(),
                                                                                                          response.getHeaders());
                                                                     })
                                                           .body(FacebookTokenResponse.class);

        if (facebookResponse == null) {
            throw new FacebookInvalidTokenException();
        }
        logger.info("Facebook accessToken: %s".formatted(facebookResponse.accessToken()));

        return facebookResponse.accessToken();
    }

}
