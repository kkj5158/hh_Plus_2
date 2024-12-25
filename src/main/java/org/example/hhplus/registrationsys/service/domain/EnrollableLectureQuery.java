package org.example.hhplus.registrationsys.service.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EnrollableLectureQuery {
  private String lectureId;
  private LocalDate lectureDate;
  private String title;
  private int vacancy;
}
