package org.example.hhplus.registrationsys.service.domain;

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
public class LectureRegQuery {

  private String userId;
  private String lectureId;
  private String title;
  private boolean successYn;
  private String message;


}
