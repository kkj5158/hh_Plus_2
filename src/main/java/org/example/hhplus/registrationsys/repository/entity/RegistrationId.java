package org.example.hhplus.registrationsys.repository.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Getter
public class RegistrationId implements Serializable {
  private String userId;
  private String lectureId;

  // equals and hashCode 구현
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegistrationId that = (RegistrationId) o;
    return Objects.equals(userId, that.userId) &&
        Objects.equals(lectureId, that.lectureId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, lectureId);
  }

}