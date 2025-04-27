package io.gozcu.notesharing.api;

import io.gozcu.notesharing.model.*;
import io.gozcu.notesharing.repository.ApiStatisticsRepository;
import io.gozcu.notesharing.service.ApiStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/statistics")
public class ApiStatisticsController {

    @Autowired
    private ApiStatisticsService apiStatisticsService;

    @Autowired
    private ApiStatisticsRepository apiStatisticsRepository;

    @GetMapping
    public ResponseEntity<StatisticsSummaryResponse> getApiStatisticsSummary() {
        try {
            long totalRequests = apiStatisticsRepository.count();

            // Endpoint statistics
            List<StatisticsSummaryResponseEndpointStatsInner> endpointStats = new ArrayList<>();
            Map<String, Long> endpointCounts = apiStatisticsService.getEndpointCounts();
            for (Map.Entry<String, Long> entry : endpointCounts.entrySet()) {
                StatisticsSummaryResponseEndpointStatsInner stat = new StatisticsSummaryResponseEndpointStatsInner();
                stat.setEndpoint(entry.getKey());
                stat.setCount(entry.getValue().intValue());
                stat.setPercentage(totalRequests > 0 ? (float)((entry.getValue() * 100.0) / totalRequests) : 0f);
                endpointStats.add(stat);
            }

            // Method statistics
            List<StatisticsSummaryResponseMethodStatsInner> methodStats = new ArrayList<>();
            Map<String, Long> methodCounts = apiStatisticsService.getMethodCounts();
            for (Map.Entry<String, Long> entry : methodCounts.entrySet()) {
                StatisticsSummaryResponseMethodStatsInner stat = new StatisticsSummaryResponseMethodStatsInner();
                stat.setMethod(entry.getKey());
                stat.setCount(entry.getValue().intValue());
                stat.setPercentage(totalRequests > 0 ? (float)((entry.getValue() * 100.0) / totalRequests) : 0f);
                methodStats.add(stat);
            }

            // Status code statistics
            List<StatisticsSummaryResponseStatusCodeStatsInner> statusCodeStats = new ArrayList<>();
            Map<Integer, Long> statusCounts = apiStatisticsService.getStatusCodeCounts();
            for (Map.Entry<Integer, Long> entry : statusCounts.entrySet()) {
                StatisticsSummaryResponseStatusCodeStatsInner stat = new StatisticsSummaryResponseStatusCodeStatsInner();
                stat.setStatusCode(entry.getKey());
                stat.setCount(entry.getValue().intValue());
                stat.setPercentage(totalRequests > 0 ? (float)((entry.getValue() * 100.0) / totalRequests) : 0f);
                statusCodeStats.add(stat);
            }

            // Time statistics
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
            LocalDateTime startOfWeek = now.toLocalDate().minusDays(7).atStartOfDay();
            LocalDateTime startOfMonth = now.toLocalDate().minusMonths(1).atStartOfDay();

            long todayCount = apiStatisticsRepository.findByRequestTimestampBetween(startOfDay, now).size();
            long weekCount = apiStatisticsRepository.findByRequestTimestampBetween(startOfWeek, now).size();
            long monthCount = apiStatisticsRepository.findByRequestTimestampBetween(startOfMonth, now).size();

            StatisticsSummaryResponseTimeStats timeStats = new StatisticsSummaryResponseTimeStats();
            timeStats.setToday((int)todayCount);
            timeStats.setLastWeek((int)weekCount);
            timeStats.setLastMonth((int)monthCount);

            // Average response time
            double averageResponseTime = apiStatisticsService.getAverageResponseTime();

            // Create the response
            StatisticsSummaryResponse response = new StatisticsSummaryResponse();
            response.setSuccess(true);
            response.setMessage("Statistics retrieved successfully");
            response.setTotalRequests((int) totalRequests);
            response.setEndpointStats(endpointStats);
            response.setMethodStats(methodStats);
            response.setStatusCodeStats(statusCodeStats);
            response.setAverageResponseTime((float) averageResponseTime);
            response.setUniqueIpAddresses(apiStatisticsService.getUniqueIpAddressCount());
            response.setUniqueUsers(apiStatisticsService.getUniqueUserCount());
            response.setTimeStats(timeStats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StatisticsSummaryResponse response = new StatisticsSummaryResponse();
            response.setSuccess(false);
            response.setMessage("Error retrieving statistics: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/details")
    public ResponseEntity<StatisticsListResponse> getApiStatisticsDetails(
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) Integer statusCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        try {
            // Create pageable for pagination
            Pageable pageable = PageRequest.of(page, size);

            // Get filtered statistics
            Page<ApiStatisticsEntity> statisticsPage = apiStatisticsService.getFilteredStatistics(
                    endpoint, method, ipAddress, statusCode, startDate, endDate, userId, pageable);

            // Convert to DTOs
            List<ApiStatisticsDTO> statisticsDTOs = statisticsPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            // Create filter information
            StatisticsListResponseFilter filter = new StatisticsListResponseFilter();
            filter.setEndpoint(endpoint);
            filter.setMethod(method);
            filter.setStatusCode(statusCode);
            filter.setIpAddress(ipAddress);

            if (startDate != null && endDate != null) {
                filter.setDateRange(startDate + " to " + endDate);
            }

            // Create response
            StatisticsListResponse response = new StatisticsListResponse();
            response.setSuccess(true);
            response.setMessage("Statistics retrieved successfully");
            response.setTotalRecords((int) statisticsPage.getTotalElements());
            response.setPage(page);
            response.setSize(size);
            response.setTotalPages(statisticsPage.getTotalPages());
            response.setFilter(filter);
            response.setStatistics(statisticsDTOs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StatisticsListResponse response = new StatisticsListResponse();
            response.setSuccess(false);
            response.setMessage("Error retrieving statistics: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    // Helper method to convert entity to DTO
    private ApiStatisticsDTO convertToDTO(ApiStatisticsEntity entity) {
        ApiStatisticsDTO dto = new ApiStatisticsDTO();
        dto.setId(entity.getId());
        dto.setEndpoint(entity.getEndpoint());
        dto.setMethod(entity.getMethod());
        dto.setIpAddress(entity.getIpAddress());
        dto.setStatusCode(entity.getStatusCode());
        dto.setResponseTimeMs(entity.getResponseTimeMs());
        dto.setUserAgent(entity.getUserAgent());

        if (entity.getRequestTimestamp() != null) {
            dto.setRequestTimestamp(entity.getRequestTimestamp().atOffset(ZoneOffset.UTC));
        }

        dto.setUserId(entity.getUserId());
        return dto;
    }
}