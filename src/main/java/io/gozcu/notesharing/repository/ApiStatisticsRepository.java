package io.gozcu.notesharing.repository;

import io.gozcu.notesharing.model.ApiStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiStatisticsRepository extends JpaRepository<ApiStatisticsEntity, Long> {
    List<ApiStatisticsEntity> findByEndpoint(String endpoint);
    List<ApiStatisticsEntity> findByIpAddress(String ipAddress);
    List<ApiStatisticsEntity> findByStatusCode(Integer statusCode);
    List<ApiStatisticsEntity> findByRequestTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<ApiStatisticsEntity> findByUserId(Long userId);
}