package org.example.hhplus.registrationsys.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.hhplus.registrationsys.controller.dto.EnrollableLectureRequest;
import org.example.hhplus.registrationsys.controller.dto.EnrollableLectureResponse;
import org.example.hhplus.registrationsys.controller.dto.LectureRegRequest;
import org.example.hhplus.registrationsys.controller.dto.LectureRegResponse;
import org.example.hhplus.registrationsys.controller.dto.UserLectureRequest;
import org.example.hhplus.registrationsys.controller.dto.UserLectureResponse;
import org.example.hhplus.registrationsys.service.RegSysService;
import org.example.hhplus.registrationsys.service.domain.EnrollableLectureQuery;
import org.example.hhplus.registrationsys.service.domain.LectureRegCommand;
import org.example.hhplus.registrationsys.service.domain.LectureRegQuery;
import org.example.hhplus.registrationsys.service.domain.UserLectureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class RegSysController {

  private static final Logger log = LoggerFactory.getLogger(RegSysController.class);
  private final RegSysService regSysService;

  @PostMapping("")
  public ResponseEntity<LectureRegResponse> applicateLecture(@RequestBody LectureRegRequest requestDto) {

    log.info("POST '/lecture' RegSysController : requet {}", requestDto.toString());

    LectureRegCommand regCommand = LectureRegCommand.builder()
                                                    .userId(requestDto.getUserId())
                                                    .lectureId(requestDto.getLectureId())
                                                    .build();

    LectureRegQuery regInfo = regSysService.applicateLecture(regCommand);

    LectureRegResponse regResponse = LectureRegResponse.builder()
                                                       .userId(regInfo.getUserId())
                                                       .letureId(regInfo.getLectureId())
                                                       .title(regInfo.getTitle())
                                                       .message(regInfo.getMessage())
                                                       .sucessYn(regInfo.isSuccessYn())
                                                       .build();

    log.info("POST '/lecture' RegSysController : response {}", regResponse.toString());

    return ResponseEntity.ok(regResponse);
  }

  @GetMapping("")
  public ResponseEntity<List<EnrollableLectureResponse>> searchEnrollableLectures(@RequestBody EnrollableLectureRequest requestDto) {

    log.info("GET '/lecture' RegSysController : requet {}", requestDto.toString());

    LectureRegCommand regCommand = LectureRegCommand.builder()
                                                    .lectureDate(requestDto.getLectureDate())
                                                    .build();

    List<EnrollableLectureQuery> regInfo = regSysService.searchEnrollableLectures(regCommand);

    List<EnrollableLectureResponse> regResponse = regInfo.stream()
                                                         .map(query -> EnrollableLectureResponse.builder()
                                                                                                .lectureId(query.getLectureId())
                                                                                                .title(query.getTitle())
                                                                                                .vacancy(query.getVacancy())
                                                                                                .build())
                                                         .toList();

    log.info("GET '/lecture' RegSysController : response {}", regResponse);

    return ResponseEntity.ok(regResponse);
  }

  @GetMapping("enrolled")
  public ResponseEntity<List<UserLectureResponse>> searchEnrolledLectures(@RequestBody UserLectureRequest requestDto) {

    log.info("GET '/lecture/enrolled' RegSysController : requet {}", requestDto.toString());

    LectureRegCommand regCommand = LectureRegCommand.builder()
                                                    .userId(requestDto.getUserId())
                                                    .build();

    List<UserLectureQuery> regInfo = regSysService.searchEnrolledLectures(regCommand);

    List<UserLectureResponse> regResponse = regInfo.stream()
                                                   .map(query -> UserLectureResponse.builder()
                                                                                    .userId(query.getUserId())
                                                                                    .lectureId(query.getLectureId())
                                                                                    .title(query.getTitle())
                                                                                    .build())
                                                   .toList();

    log.info("GET '/lecture/enrolled' RegSysController : response {}", regResponse);

    return ResponseEntity.ok(regResponse);
  }


}
