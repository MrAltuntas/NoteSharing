package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.ApiStatisticsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ApiStatisticsService {
    // Record API call
    ApiStatisticsEntity recordApiCall(String endpoint, String method, String ipAddress,
                                      Integer statusCode, Long responseTimeMs, String userAgent, Long userId);

    // Get filtered statistics with pagination
    Page<ApiStatisticsEntity> getFilteredStatistics(String endpoint, String method, String ipAddress,
                                                    Integer statusCode, LocalDateTime startDate,
                                                    LocalDateTime endDate, Long userId, Pageable pageable);

    // Summary statistics methods
    Map<String, Long> getEndpointCounts();
    Map<String, Long> getMethodCounts();
    Map<Integer, Long> getStatusCodeCounts();
    double getAverageResponseTime();
    int getUniqueIpAddressCount();
    int getUniqueUserCount();

    // Legacy methods - for convenience and backward compatibility
    List<ApiStatisticsEntity> getStatisticsByEndpoint(String endpoint);
    List<ApiStatisticsEntity> getStatisticsByIpAddress(String ipAddress);
    List<ApiStatisticsEntity> getStatisticsByStatusCode(Integer statusCode);
    List<ApiStatisticsEntity> getStatisticsByTimeRange(LocalDateTime start, LocalDateTime end);
    List<ApiStatisticsEntity> getStatisticsByUserId(Long userId);
}