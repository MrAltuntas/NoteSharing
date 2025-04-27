package io.gozcu.notesharing.service;

import io.gozcu.notesharing.model.ApiStatisticsEntity;
import io.gozcu.notesharing.repository.ApiStatisticsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApiStatisticsServiceImpl implements ApiStatisticsService {

    @Autowired
    private ApiStatisticsRepository apiStatisticsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ApiStatisticsEntity recordApiCall(String endpoint, String method, String ipAddress, Integer statusCode,
                                             Long responseTimeMs, String userAgent, Long userId) {
        ApiStatisticsEntity statistics = new ApiStatisticsEntity();
        statistics.setEndpoint(endpoint);
        statistics.setMethod(method);
        statistics.setIpAddress(ipAddress);
        statistics.setStatusCode(statusCode);
        statistics.setResponseTimeMs(responseTimeMs);
        statistics.setUserAgent(userAgent);
        statistics.setUserId(userId);
        statistics.setRequestTimestamp(LocalDateTime.now());

        return apiStatisticsRepository.save(statistics);
    }

    @Override
    public Page<ApiStatisticsEntity> getFilteredStatistics(String endpoint, String method, String ipAddress,
                                                           Integer statusCode, LocalDateTime startDate,
                                                           LocalDateTime endDate, Long userId, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApiStatisticsEntity> query = cb.createQuery(ApiStatisticsEntity.class);
        Root<ApiStatisticsEntity> root = query.from(ApiStatisticsEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        // Add filters if provided
        if (endpoint != null && !endpoint.isEmpty()) {
            predicates.add(cb.equal(root.get("endpoint"), endpoint));
        }

        if (method != null && !method.isEmpty()) {
            predicates.add(cb.equal(root.get("method"), method));
        }

        if (ipAddress != null && !ipAddress.isEmpty()) {
            predicates.add(cb.equal(root.get("ipAddress"), ipAddress));
        }

        if (statusCode != null) {
            predicates.add(cb.equal(root.get("statusCode"), statusCode));
        }

        if (startDate != null && endDate != null) {
            predicates.add(cb.between(root.get("requestTimestamp"), startDate, endDate));
        } else if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("requestTimestamp"), startDate));
        } else if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("requestTimestamp"), endDate));
        }

        if (userId != null) {
            predicates.add(cb.equal(root.get("userId"), userId));
        }

        // Apply filters and sort by timestamp desc
        query.where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("requestTimestamp")));

        // Count total results
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ApiStatisticsEntity> countRoot = countQuery.from(ApiStatisticsEntity.class);
        countQuery.select(cb.count(countRoot))
                .where(predicates.toArray(new Predicate[0]));

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // Apply pagination
        TypedQuery<ApiStatisticsEntity> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<ApiStatisticsEntity> results = typedQuery.getResultList();

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Map<String, Long> getEndpointCounts() {
        List<ApiStatisticsEntity> allStats = apiStatisticsRepository.findAll();
        return allStats.stream()
                .collect(Collectors.groupingBy(ApiStatisticsEntity::getEndpoint, Collectors.counting()));
    }

    @Override
    public Map<String, Long> getMethodCounts() {
        List<ApiStatisticsEntity> allStats = apiStatisticsRepository.findAll();
        return allStats.stream()
                .collect(Collectors.groupingBy(ApiStatisticsEntity::getMethod, Collectors.counting()));
    }

    @Override
    public Map<Integer, Long> getStatusCodeCounts() {
        List<ApiStatisticsEntity> allStats = apiStatisticsRepository.findAll();
        return allStats.stream()
                .collect(Collectors.groupingBy(ApiStatisticsEntity::getStatusCode, Collectors.counting()));
    }

    @Override
    public double getAverageResponseTime() {
        List<ApiStatisticsEntity> allStats = apiStatisticsRepository.findAll();
        return allStats.stream()
                .mapToLong(ApiStatisticsEntity::getResponseTimeMs)
                .average()
                .orElse(0.0);
    }

    @Override
    public int getUniqueIpAddressCount() {
        List<ApiStatisticsEntity> allStats = apiStatisticsRepository.findAll();
        Set<String> uniqueIps = new HashSet<>();
        for (ApiStatisticsEntity stat : allStats) {
            if (stat.getIpAddress() != null) {
                uniqueIps.add(stat.getIpAddress());
            }
        }
        return uniqueIps.size();
    }

    @Override
    public int getUniqueUserCount() {
        List<ApiStatisticsEntity> allStats = apiStatisticsRepository.findAll();
        Set<Long> uniqueUsers = new HashSet<>();
        for (ApiStatisticsEntity stat : allStats) {
            if (stat.getUserId() != null) {
                uniqueUsers.add(stat.getUserId());
            }
        }
        return uniqueUsers.size();
    }

    // Legacy methods
    @Override
    public List<ApiStatisticsEntity> getStatisticsByEndpoint(String endpoint) {
        return apiStatisticsRepository.findByEndpoint(endpoint);
    }

    @Override
    public List<ApiStatisticsEntity> getStatisticsByIpAddress(String ipAddress) {
        return apiStatisticsRepository.findByIpAddress(ipAddress);
    }

    @Override
    public List<ApiStatisticsEntity> getStatisticsByStatusCode(Integer statusCode) {
        return apiStatisticsRepository.findByStatusCode(statusCode);
    }

    @Override
    public List<ApiStatisticsEntity> getStatisticsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return apiStatisticsRepository.findByRequestTimestampBetween(start, end);
    }

    @Override
    public List<ApiStatisticsEntity> getStatisticsByUserId(Long userId) {
        return apiStatisticsRepository.findByUserId(userId);
    }
}