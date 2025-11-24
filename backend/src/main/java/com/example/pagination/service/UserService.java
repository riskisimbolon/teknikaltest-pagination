package com.example.pagination.service;

import com.example.pagination.dto.ExternalUserResponse;
import com.example.pagination.dto.PaginationResponse;
import com.example.pagination.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final int USER_LIMIT = 100;
    private final RestClient restClient;

    public PaginationResponse getPaginatedUsers(int page, int size) {
        validatePaginationParameters(page, size);

        List<User> allUsers = fetchUsersFromExternalApi();

        return buildPaginationResponse(page, size, allUsers);
    }

    private void validatePaginationParameters(int page, int size) {
        if (page <= 0 || size <= 0) {
            String errorMsg = String.format(
                    "Page and size must be greater than 0. Provided: page=%d, size=%d",
                    page, size
            );
            log.error("Validation failed: {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    private List<User> fetchUsersFromExternalApi() {
        try {
            log.info("Fetching users from external API with limit={}", USER_LIMIT);

            ExternalUserResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users")
                            .queryParam("limit", USER_LIMIT)
                            .build())
                    .retrieve()
                    .body(ExternalUserResponse.class);

            if (response == null || response.getUsers() == null) {
                log.error("External API returned null or empty response");
                throw new RuntimeException("External API returned invalid response");
            }

            log.info("Successfully fetched {} users from external API", response.getUsers().size());
            return response.getUsers();

        } catch (RestClientException e) {
            log.error("Failed to reach external API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reach external API: " + e.getMessage(), e);
        }
    }

    private PaginationResponse buildPaginationResponse(int page, int size, List<User> allUsers) {
        int totalItems = allUsers.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        List<User> pageData = getPageData(page, size, allUsers);

        log.info("Returning page {} of {} with {} items", page, totalPages, pageData.size());

        return new PaginationResponse(page, size, totalItems, totalPages, pageData);
    }

    private List<User> getPageData(int page, int size, List<User> allUsers) {
        int start = (page - 1) * size;

        if (start >= allUsers.size()) {
            return Collections.emptyList();
        }

        int end = Math.min(start + size, allUsers.size());
        return allUsers.subList(start, end);
    }
}