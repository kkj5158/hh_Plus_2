package org.example.hhplus.registrationsys.repository;

import org.example.hhplus.registrationsys.repository.entity.Registration;
import org.example.hhplus.registrationsys.repository.entity.User;
import org.example.hhplus.registrationsys.repository.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, String> {

  boolean existsByUserAndLecture(User user, Lecture lecture);

  @Query("""
        SELECT r FROM Registration r
        JOIN FETCH r.lecture l
        WHERE r.user.userId = :userId
        """)
  List<Registration> findRegistrationsByUserId(@Param("userId") String userId);
  @Query("""
        SELECT r FROM Registration r
        JOIN FETCH r.lecture l
        WHERE r.lecture.lectureId = :lectureId
        """)
  List<Registration> findRegistrationsByLectureId(@Param("lectureId") String lectureId);
}
