package org.example.hhplus.registrationsys.controller.dto;

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
public class EnrollableLectureResponse {

  private String lectureId;
  private String title;
  private int vacancy;

}
