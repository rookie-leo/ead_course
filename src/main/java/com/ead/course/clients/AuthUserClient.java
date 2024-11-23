package com.ead.course.clients;

import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserRecordDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    final RestClient restClient;

    @Value("${ead.api.url.authuser}")
    String baseUrlAuthuser;

    public AuthUserClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public Page<UserRecordDto> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        String url = baseUrlAuthuser + "/users?courseId=" + "&page=" + pageable.getPageNumber() + "&size="
                + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
        log.debug("Request to URL: {}", url);

        try {
            return restClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ResponsePageDto<UserRecordDto>>() {});
        } catch (RestClientException ex) {
            log.error("Error Request RestClient with cause: {}", ex.getMessage());
            throw new RuntimeException("Error Request RestClient", ex);
        }
    }
}
