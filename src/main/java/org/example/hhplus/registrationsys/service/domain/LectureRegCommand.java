package org.example.hhplus.registrationsys.service.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class LectureRegCommand {
  private String userId;
  private String lectureId;
  private String lectureDate;
  private LocalDateTime dateTime;
}
