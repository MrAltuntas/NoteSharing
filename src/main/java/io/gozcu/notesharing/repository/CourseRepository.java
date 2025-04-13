package io.gozcu.notesharing.repository;

import io.gozcu.notesharing.model.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    List<CourseEntity> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT c FROM CourseEntity c JOIN c.tags t WHERE t = :tag")
    List<CourseEntity> findByTag(@Param("tag") String tag);

    @Query("SELECT c FROM CourseEntity c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "EXISTS (SELECT t FROM c.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<CourseEntity> search(@Param("query") String query);
}