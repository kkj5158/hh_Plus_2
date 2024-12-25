package org.example.hhplus.registrationsys.repository;

import org.example.hhplus.registrationsys.repository.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, String> {

  @Query("SELECT l FROM Lecture l WHERE l.lectureId = :lectureId")
  Optional<Lecture> findLectureInfoByLectureId(@Param("lectureId") String lectureId);

  @Query(value = """
        SELECT l.* FROM Lecture l
        WHERE l.lecture_dttm BETWEEN :startOfDay AND :endOfDay
        AND l.vacancy > 0
        """, nativeQuery = true)
  List<Lecture> findEnrollableLecturesByDateRange(@Param("startOfDay") String startOfDay, @Param("endOfDay") String endOfDay);

  @Modifying
  @Query("UPDATE Lecture l SET l.vacancy = l.vacancy - 1 WHERE l.lectureId = :lectureId AND l.vacancy > 0")
  int decreaseVacancy(@Param("lectureId") String lectureId);
}
