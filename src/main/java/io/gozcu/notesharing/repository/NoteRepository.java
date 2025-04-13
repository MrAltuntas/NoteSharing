package io.gozcu.notesharing.repository;

import io.gozcu.notesharing.model.NoteEntity;
import io.gozcu.notesharing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByCourseId(Long courseId);
    List<NoteEntity> findByAuthor(User author);
    List<NoteEntity> findByCourseIdAndAuthor(Long courseId, User author);
}