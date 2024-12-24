package org.example.hhplus.registrationsys.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.hhplus.registrationsys.controller.RegSysController;
import org.example.hhplus.registrationsys.exception.DefaultException;
import org.example.hhplus.registrationsys.helper.DateHelper;
import org.example.hhplus.registrationsys.repository.RegRepository;
import org.example.hhplus.registrationsys.repository.entity.Lecture;
import org.example.hhplus.registrationsys.repository.entity.Registration;
import org.example.hhplus.registrationsys.repository.entity.RegistrationId;
import org.example.hhplus.registrationsys.repository.entity.User;
import org.example.hhplus.registrationsys.service.domain.EnrollableLectureQuery;
import org.example.hhplus.registrationsys.service.domain.LectureRegCommand;
import org.example.hhplus.registrationsys.service.domain.LectureRegQuery;
import org.example.hhplus.registrationsys.service.domain.UserLectureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegSysServiceImpl implements RegSysService {

  private static final Logger log = LoggerFactory.getLogger(RegSysController.class);
  private final RegRepository regRepository;
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


  public User validateUserInfo(String userId) {
    log.info("RegSysService - validateUserInfo: request {}", userId);

    User userInfo;

    // 검증
    if (regRepository.findUserInfoByUserId(userId)
                     .isPresent()) {
      userInfo = regRepository.findUserInfoByUserId(userId)
                              .get();
    } else {
      throw new DefaultException(HttpStatus.BAD_REQUEST, "존재하지 않는 유저Id 입니다.");
    }

    log.info("RegSysService - validateUserInfo: response {}", userInfo);

    return userInfo;

  }

  public Lecture validateLectureInfo(String lectureId) {

    log.info("RegSysService - validateLectureInfo: request {}", lectureId);

    Lecture lectureInfo;

    // 검증
    if (regRepository.findLectureInfoByLectureId(lectureId)
                     .isPresent()) {
      lectureInfo = regRepository.findLectureInfoByLectureId(lectureId)
                                 .get();
    } else {
      throw new DefaultException(HttpStatus.BAD_REQUEST, "존재하지 않는 강의Id 입니다.");
    }

    log.info("RegSysService - lectureInfo: response {}", lectureInfo);

    return lectureInfo;

  }


  @Override
  @Transactional
  public LectureRegQuery applicateLecture(LectureRegCommand command) {

    log.info("RegSysService - applicateLecture: command {}", command);

    User userInfo = validateUserInfo(command.getUserId());
    Lecture lectureInfo = validateLectureInfo(command.getLectureId());

    // 사용자와 강의 엔티티 생성

    // 중복 신청 확인
    if (regRepository.existsByUserAndLecture(userInfo, lectureInfo)) {
      log.warn("사용자 {}가 이미 강의 {}에 신청했습니다.", userInfo.getUserId(), lectureInfo.getLectureId());
      LectureRegQuery response = LectureRegQuery.builder()
                                                .userId(userInfo.getUserId())
                                                .lectureId(lectureInfo.getLectureId())
                                                .title(lectureInfo.getTitle())
                                                .successYn(false)
                                                .message("이미 신청된 강의입니다.")
                                                .build();
      log.info("RegSysService - applicateLecture: response {}", response.toString());
      return response;
    }

    // 강의 정원 확인
    int updatedRows = regRepository.decreaseVacancy(lectureInfo.getLectureId());

    if (updatedRows == 0) {

      log.warn("강의 {}의 정원이 초과되었습니다.", lectureInfo.getLectureId());
      LectureRegQuery response = LectureRegQuery.builder()
                                                .userId(userInfo.getUserId())
                                                .lectureId(lectureInfo.getLectureId())
                                                .title(lectureInfo.getTitle())
                                                .successYn(false)
                                                .message("강의 정원이 초과되었습니다.")
                                                .build();
      log.info("RegSysService - applicateLecture: response {}", response.toString());
      return response;
    }

    // 신청 저장

    RegistrationId registrationId = new RegistrationId(userInfo.getUserId(), lectureInfo.getLectureId());

    Registration registration = Registration.builder()
                                            .id(registrationId)
                                            .user(userInfo)
                                            .lecture(lectureInfo)
                                            .registrationDttm(LocalDateTime.now())
                                            .build();

    regRepository.save(registration);

    LectureRegQuery query = LectureRegQuery.builder()
                                           .userId(registration.getUser()
                                                               .getUserId())
                                           .lectureId(registration.getLecture()
                                                                  .getLectureId())
                                           .title(registration.getLecture()
                                                              .getTitle())
                                           .successYn(true)
                                           .message("신청 성공")
                                           .build();

    log.info("RegSysService - applicateLecture: query {}", query.toString());

    return query;
  }

  @Override
  public List<EnrollableLectureQuery> searchEnrollableLectures(LectureRegCommand command) {

    log.info("RegSysService - searchEnrollableLectures: command {}", command);

    // 특정 날짜에 해당하는 강의 목록 조회
    String lectureDate = command.getLectureDate();

    String startOfDay = DateHelper.setStartOfDay(lectureDate);
    String endOfDay = DateHelper.setEndOfDay(lectureDate);

    List<Lecture> lectures = regRepository.findEnrollableLecturesByDateRange(startOfDay, endOfDay);

    if (lectures.size() == 0) {
      throw new DefaultException(HttpStatus.NOT_FOUND, "해당 날짜에 강의가 존재하지 않습니다.");
    }

    // 응답 데이터 변환
    List<EnrollableLectureQuery> queries = lectures.stream()
                                                   .map(lecture -> EnrollableLectureQuery.builder()
                                                                                         .lectureId(lecture.getLectureId())
                                                                                         .title(lecture.getTitle())
                                                                                         .vacancy(lecture.getCapacity())
                                                                                         .build())
                                                   .collect(Collectors.toList());

    log.info("RegSysService - searchEnrollableLectures: queries {}", queries);

    return queries;
  }

  @Override
  public List<UserLectureQuery> searchEnrolledLectures(LectureRegCommand command) {
    log.info("RegSysService - searchEnrolledLectures: command {}", command.toString());

    User userInfo = validateUserInfo(command.getUserId());

    // 사용자 신청 내역 조회
    String userId = userInfo.getUserId();

    List<Registration> registrations = regRepository.findRegistrationsByUserId(userId);

    // 응답 데이터 변환
    List<UserLectureQuery> queries = registrations.stream()
                                                  .map(reg -> UserLectureQuery.builder()
                                                                              .userId(reg.getUser()
                                                                                         .getUserId())
                                                                              .lectureId(reg.getLecture()
                                                                                            .getLectureId())
                                                                              .title(reg.getLecture()
                                                                                        .getTitle())
                                                                              .build())
                                                  .collect(Collectors.toList());

    log.info("RegSysService - searchEnrolledLectures: queries {}", queries);
    return queries;
  }
}



