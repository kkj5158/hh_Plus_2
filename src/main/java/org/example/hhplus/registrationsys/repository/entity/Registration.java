package org.example.hhplus.registrationsys.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {
  @EmbeddedId
  private RegistrationId id;

  @ManyToOne
  @MapsId("userId") // RegistrationId의 userId와 매핑
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @MapsId("lectureId") // RegistrationId의 lectureId와 매핑
  @JoinColumn(name = "lecture_id")
  private Lecture lecture;

  private LocalDateTime registrationDttm;


}