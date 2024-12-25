package org.example.hhplus.registrationsys.integreationTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.hhplus.registrationsys.repository.LectureRepository;
import org.example.hhplus.registrationsys.repository.RegistrationRepository;
import org.example.hhplus.registrationsys.repository.UserRepository;
import org.example.hhplus.registrationsys.repository.entity.Lecture;
import org.example.hhplus.registrationsys.repository.entity.Registration;
import org.example.hhplus.registrationsys.repository.entity.RegistrationId;
import org.example.hhplus.registrationsys.repository.entity.User;
import org.example.hhplus.registrationsys.service.RegSysServiceImpl;
import org.example.hhplus.registrationsys.service.domain.EnrollableLectureQuery;
import org.example.hhplus.registrationsys.service.domain.LectureRegCommand;
import org.example.hhplus.registrationsys.service.domain.LectureRegQuery;
import org.example.hhplus.registrationsys.service.domain.UserLectureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class RegSysServiceIntegrationTest {

  @Autowired
  private RegSysServiceImpl regSysService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private RegistrationRepository registrationRepository;

  private User testUser;
  private Lecture testLecture;

  private String generateRandomId(String base) {
    int randomNumber = (int) (Math.random() * 100000); // 0부터 99999까지의 랜덤 숫자 생성
    return base + String.format("%05d", randomNumber); // 5자리로 포맷팅하여 base와 결합
  }
  @BeforeEach
  public void setUp() {
    // 테스트용 사용자 생성
    testUser = User.builder()
                   .userId(generateRandomId("testUser"))
                   .userNm("테스트 사용자")
                   .build();
    userRepository.save(testUser);

    // 테스트용 강의 생성
    testLecture = Lecture.builder()
                         .lectureId(generateRandomId("testLecture"))
                         .title("테스트 강의")
                         .lecturer("테스트 강사")
                         .lectureDttm(LocalDateTime.now().plusDays(1))
                         .capacity(30)
                         .vacancy(30)
                         .build();
    lectureRepository.save(testLecture);
  }

  @Test
  public void 랜덤유저_랜덤강의_신청_Success() {
    // Given
    LectureRegCommand command = new LectureRegCommand();
    command.setUserId(testUser.getUserId());
    command.setLectureId(testLecture.getLectureId());

    // When
    LectureRegQuery result = regSysService.applicateLecture(command);

    // Then
    assertThat(result.isSuccessYn()).isTrue();
    assertThat(result.getMessage()).isEqualTo("신청 성공");

    // 등록 정보 확인
    Optional<Registration> registration = registrationRepository.findById(testUser.getUserId());
    assertThat(registration).isPresent();

    // 강의 잔여석 확인
    Lecture updatedLecture = lectureRepository.findById(testLecture.getLectureId())
                                              .orElseThrow();

    assertThat(updatedLecture.getVacancy()).isEqualTo(29);
  }

  @Test
  public void 랜덤강의_랜덤유저_이미신청() {
    // Given
    Registration registration = new Registration();
    registration.setId(new RegistrationId(testUser.getUserId(), testLecture.getLectureId()));
    registration.setUser(testUser);
    registration.setLecture(testLecture);
    registration.setRegistrationDttm(LocalDateTime.now());
    registrationRepository.save(registration);

    LectureRegCommand command = new LectureRegCommand();
    command.setUserId(testUser.getUserId());
    command.setLectureId(testLecture.getLectureId());

    // When
    LectureRegQuery result = regSysService.applicateLecture(command);

    // Then
    assertThat(result.isSuccessYn()).isFalse();
    assertThat(result.getMessage()).isEqualTo("이미 신청된 강의입니다.");
  }

  @Test
  public void 랜덤유저_랜덤강의_빈자리없음() {
    // Given
    testLecture.setVacancy(0);
    lectureRepository.save(testLecture);

    LectureRegCommand command = new LectureRegCommand();
    command.setUserId(testUser.getUserId());
    command.setLectureId(testLecture.getLectureId());

    // When
    LectureRegQuery result = regSysService.applicateLecture(command);

    // Then
    assertThat(result.isSuccessYn()).isFalse();
    assertThat(result.getMessage()).isEqualTo("강의 정원이 초과되었습니다.");
  }

  @Test
  public void 현재_수강신청가능한_강의조회() {
    // Given
    LectureRegCommand command = new LectureRegCommand();
    command.setLectureDate(testLecture.getLectureDttm()
                                      .toLocalDate()
                                      .toString());

    // When
    List<EnrollableLectureQuery> lectures = regSysService.searchEnrollableLectures(command);

    // Then
    assertThat(lectures).isNotEmpty();
    assertThat(lectures.get(0)
                       .getLectureId()).isEqualTo(testLecture.getLectureId());
  }


  @Test
  public void 등록한_강의들_조회() {
    // Given
    Registration registration = new Registration();
    registration.setId(new RegistrationId(testUser.getUserId(), testLecture.getLectureId()));
    registration.setUser(testUser);
    registration.setLecture(testLecture);
    registration.setRegistrationDttm(LocalDateTime.now());
    registrationRepository.save(registration);

    LectureRegCommand command = new LectureRegCommand();
    command.setUserId(testUser.getUserId());

    // When
    List<UserLectureQuery> lectures = regSysService.searchEnrolledLectures(command);

    // Then
    assertThat(lectures).isNotEmpty();
    assertThat(lectures.get(0)
                       .getLectureId()).isEqualTo(testLecture.getLectureId());
  }
}
