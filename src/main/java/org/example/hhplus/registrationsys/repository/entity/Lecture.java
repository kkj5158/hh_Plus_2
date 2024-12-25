package org.example.hhplus.registrationsys.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import org.example.hhplus.registrationsys.exception.DefaultException;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {

  @Id
  private String lectureId;

  private String title;

  private String lecturer;

  private LocalDateTime lectureDttm;

  private int capacity = 30;

  private int vacancy;

  @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Registration> registrations;

  public void decreaseVacancy() {
    if (this.vacancy > 0) {
      this.vacancy--;
    } else {
      throw new DefaultException(HttpStatus.CONFLICT, "잔여석이 없습니다.");
    }
  }
}