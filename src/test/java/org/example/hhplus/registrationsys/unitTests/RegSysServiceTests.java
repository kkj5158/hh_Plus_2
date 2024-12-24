package org.example.hhplus.registrationsys.unitTests;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.hhplus.registrationsys.exception.DefaultException;
import org.example.hhplus.registrationsys.repository.RegRepository;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegSysServiceTests {

  @InjectMocks
  private RegSysServiceImpl regSysService;

  @Mock
  private RegRepository regRepository;

  private User user;
  private Lecture lecture;

  @BeforeEach
  void setUp() {
    user = User.builder()
               .userId("123")
               .userNm("John Doe")
               .build();

    lecture = Lecture.builder()
                     .lectureId("L101")
                     .title("Advanced Math")
                     .capacity(10)
                     .build();
  }

  @Test
  void 강의지원_Success() {
    // given
    LectureRegCommand command = LectureRegCommand.builder()
                                                 .userId(user.getUserId())
                                                 .lectureId(lecture.getLectureId())
                                                 .build();


    when(regRepository.findUserInfoByUserId(user.getUserId())).thenReturn(Optional.of(user));
    when(regRepository.findLectureInfoByLectureId(lecture.getLectureId())).thenReturn(Optional.of(lecture));
    when(regRepository.existsByUserAndLecture(user, lecture)).thenReturn(false);
    when(regRepository.decreaseVacancy(lecture.getLectureId())).thenReturn(1);

    // when
    LectureRegQuery result = regSysService.applicateLecture(command);

    // then
    assertThat(result).isNotNull();
    assertThat(result.isSuccessYn()).isTrue();
    assertThat(result.getMessage()).isEqualTo("신청 성공");

    verify(regRepository).save(argThat(reg ->
        reg.getUser().equals(user) &&
            reg.getLecture().equals(lecture)
    ));
  }

  @Test
  void 강의지원_이미등록완료() {
    // given
    LectureRegCommand command = LectureRegCommand.builder()
                                                 .userId(user.getUserId())
                                                 .lectureId(lecture.getLectureId())
                                                 .build();

    when(regRepository.findUserInfoByUserId(user.getUserId())).thenReturn(Optional.of(user));
    when(regRepository.findLectureInfoByLectureId(lecture.getLectureId())).thenReturn(Optional.of(lecture));
    when(regRepository.existsByUserAndLecture(user, lecture)).thenReturn(true);

    // when
    LectureRegQuery result = regSysService.applicateLecture(command);

    // then
    assertThat(result).isNotNull();
    assertThat(result.isSuccessYn()).isFalse();
    assertThat(result.getMessage()).isEqualTo("이미 신청된 강의입니다.");
    verify(regRepository, never()).save(any());
  }

  @Test
  void 강의지원_모두다찼을때() {
    // given
    LectureRegCommand command = LectureRegCommand.builder()
                                                 .userId(user.getUserId())
                                                 .lectureId(lecture.getLectureId())
                                                 .build();

    when(regRepository.findUserInfoByUserId(user.getUserId())).thenReturn(Optional.of(user));
    when(regRepository.findLectureInfoByLectureId(lecture.getLectureId())).thenReturn(Optional.of(lecture));
    when(regRepository.existsByUserAndLecture(user, lecture)).thenReturn(false);
    when(regRepository.decreaseVacancy(lecture.getLectureId())).thenReturn(0);

    // when
    LectureRegQuery result = regSysService.applicateLecture(command);

    // then
    assertThat(result).isNotNull();
    assertThat(result.isSuccessYn()).isFalse();
    assertThat(result.getMessage()).isEqualTo("강의 정원이 초과되었습니다.");
    verify(regRepository, never()).save(any());
  }

  @Test
  void 지원가능한_강의조회_Success() {
    // given
    String lectureDate = "2024-12-24";
    List<Lecture> lectures = List.of(
        Lecture.builder().lectureId("L101").title("Math 101").capacity(5).build(),
        Lecture.builder().lectureId("L102").title("Physics 101").capacity(3).build()
    );

    when(regRepository.findEnrollableLecturesByDateRange(any(), any())).thenReturn(lectures);

    // when
    List<EnrollableLectureQuery> result = regSysService.searchEnrollableLectures(
        LectureRegCommand.builder().lectureDate(lectureDate).build()
    );

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getLectureId()).isEqualTo("L101");
    verify(regRepository).findEnrollableLecturesByDateRange(any(), any());
  }

  @Test
  void 유저등록강의목록조회_Success() {
    // given
    List<Registration> registrations = List.of(
        Registration.builder()
                    .id(new RegistrationId(user.getUserId(), "L101"))
                    .user(user)
                    .lecture(Lecture.builder().lectureId("L101").title("Math 101").build())
                    .build(),
        Registration.builder()
                    .id(new RegistrationId(user.getUserId(), "L102"))
                    .user(user)
                    .lecture(Lecture.builder().lectureId("L102").title("Physics 101").build())
                    .build()
    );

    when(regRepository.findUserInfoByUserId(user.getUserId())).thenReturn(Optional.ofNullable(user));

    when(regRepository.findRegistrationsByUserId(user.getUserId())).thenReturn(registrations);

    // when
    List<UserLectureQuery> result = regSysService.searchEnrolledLectures(
        LectureRegCommand.builder().userId(user.getUserId()).build()
    );

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getUserId()).isEqualTo("123");
    assertThat(result.get(1).getUserId()).isEqualTo("123");
    assertThat(result.get(0).getLectureId()).isEqualTo("L101");
    assertThat(result.get(1).getLectureId()).isEqualTo("L102");

  }

  @Test
  void 유효하지않은_유저아이디_NotFound() {
    // given
    String invalidUserId = "999";
    when(regRepository.findUserInfoByUserId(invalidUserId)).thenReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> regSysService.validateUserInfo(invalidUserId))
        .isInstanceOf(DefaultException.class)
        .hasMessageContaining("존재하지 않는 유저Id 입니다.");
  }

  @Test
  void 유효하지않은_강의아이디_NotFound() {
    // given
    String invalidLectureId = "999";
    when(regRepository.findLectureInfoByLectureId(invalidLectureId)).thenReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> regSysService.validateLectureInfo(invalidLectureId))
        .isInstanceOf(DefaultException.class)
        .hasMessageContaining("존재하지 않는 강의Id 입니다.");
  }
}
