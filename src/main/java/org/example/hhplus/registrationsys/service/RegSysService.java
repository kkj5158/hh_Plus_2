package org.example.hhplus.registrationsys.service;

import java.util.List;
import org.example.hhplus.registrationsys.service.domain.EnrollableLectureQuery;
import org.example.hhplus.registrationsys.service.domain.LectureRegCommand;
import org.example.hhplus.registrationsys.service.domain.LectureRegQuery;
import org.example.hhplus.registrationsys.service.domain.UserLectureQuery;

public interface RegSysService {

  public LectureRegQuery applicateLecture(LectureRegCommand command);

  public List<EnrollableLectureQuery> searchEnrollableLectures(LectureRegCommand command);

  public List<UserLectureQuery> searchEnrolledLectures(LectureRegCommand command);

}
