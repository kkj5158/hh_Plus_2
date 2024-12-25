package org.example.hhplus.registrationsys.repository.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  private String userId;
  private String userNm;
}