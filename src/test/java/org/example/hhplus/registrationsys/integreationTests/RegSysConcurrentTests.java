package org.example.hhplus.registrationsys.integreationTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.hhplus.registrationsys.repository.LectureRepository;
import org.example.hhplus.registrationsys.repository.RegistrationRepository;
import org.example.hhplus.registrationsys.repository.UserRepository;
import org.example.hhplus.registrationsys.repository.entity.Lecture;
import org.example.hhplus.registrationsys.repository.entity.Registration;
import org.example.hhplus.registrationsys.repository.entity.User;
import org.example.hhplus.registrationsys.service.RegSysServiceImpl;
import org.example.hhplus.registrationsys.service.domain.LectureRegCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RegSysConcurrentTests {

  @Autowired
  private RegSysServiceImpl regSysService;
  @Autowired
  private RegistrationRepository registrationRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LectureRepository lectureRepository;

  private Lecture lecture;

  private String generateRandomId(String base) {
    int randomNumber = (int) (Math.random() * 100000); // 0부터 99999까지의 랜덤 숫자 생성
    return base + String.format("%05d", randomNumber); // 5자리로 포맷팅하여 base와 결합
  }

  @BeforeEach
  void setUp() {
    // 강의 생성
    lecture = Lecture.builder()
                     .lectureId(generateRandomId("Lecture_test"))
                     .title("Concurrent Programming")
                     .lecturer("명강사")
                     .capacity(30) // 정원 30명
                     .vacancy(5)   // 공석 5명
                     .lectureDttm(LocalDateTime.now().plusDays(1))
                     .build();

    lectureRepository.save(lecture);
  }

  @Test
  void testConcurrentLectureApplication() throws InterruptedException {

    int numberOfThreads = 10000; // 동시 요청 수
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);

    // 모든 스레드에서 강의 신청 요청
    for (int i = 0; i < numberOfThreads; i++) {
      executorService.submit(() -> {
        try {
          // 고유한 사용자 생성
          User user = User.builder()
                          .userId(generateRandomId("testUser"))
                          .userNm("테스트 사용자")
                          .build();
          userRepository.save(user);

          // 강의 신청 커맨드 생성
          LectureRegCommand command = LectureRegCommand.builder()
                                                       .userId(user.getUserId())
                                                       .lectureId(lecture.getLectureId())
                                                       .build();

          // 강의 신청
          regSysService.applicateLecture(command);
        } catch (Exception e) {
          System.out.println("Exception: " + e.getMessage());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await(); // 모든 스레드가 완료될 때까지 대기
    executorService.shutdown();

    // 최종 강의 잔여석 확인
    Lecture updatedLecture = lectureRepository.findById(lecture.getLectureId())
                                              .orElseThrow();

    assertThat(updatedLecture.getVacancy()).isEqualTo(0); // 잔여석이 0이어야 함

    // 신청된 수 확인
    List<Registration> registrationCount = registrationRepository.findRegistrationsByLectureId(lecture.getLectureId());
    assertThat(registrationCount.size()).isEqualTo(5); // 5명의 사용자가 성공적으로 신청했어야 함
  }
}
