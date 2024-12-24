package org.example.hhplus.registrationsys.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.example.hhplus.registrationsys.repository.entity.Lecture;
import org.example.hhplus.registrationsys.repository.entity.Registration;
import org.example.hhplus.registrationsys.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface RegRepository extends JpaRepository<Registration, String> {

  boolean existsByUserAndLecture(User user, Lecture lecture);

  // 등록되어있는지 확인

  @Modifying
  @Query("UPDATE Lecture l SET l.vacancy = l.vacancy - 1 WHERE l.lectureId = :lectureId AND l.vacancy > 0")
  int decreaseVacancy(@Param("lectureId") String lectureId);

  @Query(value = """
       SELECT l.lecture_id, l.capacity, l.vacancy, l.lecture_dttm, l.lecturer , l.title FROM Lecture l 
       WHERE CONVERT(l.lecture_dttm, DATETIME) BETWEEN :startOfDay AND :endOfDay
       AND l.vacancy > 0
       """, nativeQuery = true)
  List<Lecture> findEnrollableLecturesByDateRange(@Param("startOfDay") String startOfDay, @Param("endOfDay")String endOfDay);
  @Query("""
           SELECT r FROM Registration r
           JOIN FETCH r.lecture l
           WHERE r.user.userId = :userId
           """)
  List<Registration> findRegistrationsByUserId(String userId);

  @Query("SELECT l FROM Lecture l WHERE l.lectureId = :lectureId")
  Optional<Lecture> findLectureInfoByLectureId(@Param("lectureId") String lectureId);
  // userId로 사용자 정보 조회
  @Query("SELECT u FROM User u WHERE u.userId = :userId")
  Optional<User> findUserInfoByUserId(@Param("userId") String userId);


}
