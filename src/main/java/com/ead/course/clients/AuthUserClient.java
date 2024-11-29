package com.ead.course.clients;

import com.ead.course.dtos.CourseUserRecordDto;
import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserRecordDto;
import com.ead.course.exceptions.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

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
                    .body(new ParameterizedTypeReference<ResponsePageDto<UserRecordDto>>() {
                    });
        } catch (RestClientException ex) {
            log.error("Error Request RestClient with cause: {}", ex.getMessage());
            throw new RuntimeException("Error Request RestClient", ex);
        }
    }

    public ResponseEntity<UserRecordDto> getOneUsreById(UUID userId) {
        String url = baseUrlAuthuser + "/users/" + userId;
        log.debug("Request to URL: {}", url);

        return restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 404, ((request, response) -> {
                    log.error("User not found!: {}", userId);
                    throw new NotFoundException("User not found!");
                }))
                .toEntity(UserRecordDto.class);
    }

    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = baseUrlAuthuser + "/users/" + userId + "/courses/subscription";
        log.debug("POST Request to URL: {}", url);

        try {
            var courseUserRecordDto = new CourseUserRecordDto(courseId, userId);

            restClient.post()
                    .uri(url)
                    .contentType(APPLICATION_JSON)
                    .body(courseUserRecordDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Error Request POST RestClient with cause: {}", ex.getMessage());
            throw new RuntimeException("Error Request POST RestClient", ex);
        }
    }

    public void deleteCourseUserInAuthUser(UUID courseId) {
        String url = baseUrlAuthuser + "/users/courses/" + courseId;
        log.debug("DELETE Request to URL: {}", url);

        try {
            restClient.delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.error("Error Request DELETE RestClient with cause: {}", ex.getMessage());
            throw new RuntimeException("Error Request DELETE RestClient", ex);
        }
    }
}
