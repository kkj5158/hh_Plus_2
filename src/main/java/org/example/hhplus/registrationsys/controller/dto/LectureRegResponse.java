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
public class LectureRegResponse {

  private String userId;
  private String letureId;
  private String title;
  private boolean sucessYn;
  private String message;


}
