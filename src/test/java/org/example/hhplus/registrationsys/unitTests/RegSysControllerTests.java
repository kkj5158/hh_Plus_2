package org.example.hhplus.registrationsys.unitTests;


import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.example.hhplus.registrationsys.controller.RegSysController;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@ExtendWith(MockitoExtension.class)
public class RegSysControllerTests {

  private MockMvc mockMvc;

  @InjectMocks
  private RegSysController regSysController;

  @Mock
  private RegSysService regSysService;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = MockMvcBuilders.standaloneSetup(regSysController).build();
  }


  @Test
  void 특강신청_테스트() throws Exception {
    //given

    // 요청 DTO
    LectureRegRequest request = LectureRegRequest.builder()
                                                 .userId("123")
                                                 .lectureId("L101")
                                                 .build();

    LectureRegCommand command = LectureRegCommand.builder()
                                                 .userId(request.getUserId())
                                                 .lectureId(request.getLectureId())
                                                 .build();

    //when

    // 서비스 레이어의 반환 값
    LectureRegQuery query = LectureRegQuery.builder()
                                   .userId("123")
                                           .lectureId("L101")
                                           .title("Advanced Math")
                                           .successYn(true)
                                           .message("Lecture successfully registered")
                                           .build();

    when(regSysService.applicateLecture(argThat(cmd ->
        "123".equals(command.getUserId()) &&
            "L101".equals(command.getLectureId())
    ))).thenReturn(query);


    // 기대 응답 DTO
    LectureRegResponse response = LectureRegResponse.builder()
                                                    .userId("123")
                                                    .letureId("L101")
                                                    .title("Advanced Math")
                                                    .sucessYn(true)
                                                    .message("Lecture successfully registered")
                                                    .build();

    //then
    mockMvc.perform(post("/lecture").contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(content().json(objectMapper.writeValueAsString(response)));
  }

  @Test
  void 유저_신청가능한특강_조회() throws Exception {
    // 요청 DTO
    EnrollableLectureRequest request = EnrollableLectureRequest.builder()
                                                               .lectureDate("2024-12-24")
                                                               .build();

    // 서비스 레이어의 반환 값
    List<EnrollableLectureQuery> queryResult = List.of(
        EnrollableLectureQuery.builder()
                              .lectureId("L101")
                              .title("Advanced Math")
                              .vacancy(5)
                              .build(),
        EnrollableLectureQuery.builder()
                              .lectureId("L102")
                              .title("Physics 101")
                              .vacancy(3)
                              .build()
    );

    // 모킹 설정
    when(regSysService.searchEnrollableLectures(argThat(cmd ->
        "2024-12-24".equals(cmd.getLectureDate())
    ))).thenReturn(queryResult);

    // 기대 응답 DTO
    List<EnrollableLectureResponse> response = queryResult.stream()
                                                          .map(q -> EnrollableLectureResponse.builder()
                                                                                             .lectureId(q.getLectureId())
                                                                                             .title(q.getTitle())
                                                                                             .vacancy(q.getVacancy())
                                                                                             .build())
                                                          .toList();

    mockMvc.perform(get("/lecture").contentType(MediaType.APPLICATION_JSON)
                                   .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(content().json(objectMapper.writeValueAsString(response)));
  }

  @Test
  void 유저_특강목록_조회() throws Exception {
    // 요청 DTO
    UserLectureRequest request = UserLectureRequest.builder()
                                                   .userId("123")
                                                   .build();

    // 서비스 레이어의 반환 값
    List<UserLectureQuery> queryResult = List.of(
        UserLectureQuery.builder()
                        .userId("123")
                        .lectureId("L101")
                        .title("Advanced Math")
                        .build(),
        UserLectureQuery.builder()
                        .userId("123")
                        .lectureId("L102")
                        .title("Physics 101")
                        .build()
    );

    // 모킹 설정
    when(regSysService.searchEnrolledLectures(argThat(cmd ->
        "123".equals(cmd.getUserId())
    ))).thenReturn(queryResult);

    // 기대 응답 DTO
    List<UserLectureResponse> response = queryResult.stream()
                                                    .map(q -> UserLectureResponse.builder()
                                                                                 .userId(q.getUserId())
                                                                                 .lectureId(q.getLectureId())
                                                                                 .title(q.getTitle())
                                                                                 .build())
                                                    .toList();

    mockMvc.perform(get("/lecture/enrolled").contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(content().json(objectMapper.writeValueAsString(response)));
  }
}


