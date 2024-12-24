package org.example.hhplus.registrationsys.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class DateHelper {

  // 입력받은 날짜에서 시간대를 제거하고 00:00:00으로 변환
  public static String setStartOfDay(String dateTime) {
    LocalDate date = extractDate(dateTime);
    return date + " 00:00:00";
  }

  // 입력받은 날짜에서 시간대를 제거하고 23:59:59으로 변환
  public static String setEndOfDay(String dateTime) {
    LocalDate date = extractDate(dateTime);
    return date + " 23:59:59";
  }

  // 날짜 부분만 추출하는 메서드 (두 가지 형식 처리)
  private static LocalDate extractDate(String dateTime) {
    try {
      // 형식 1: yyyy-MM-dd
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      return LocalDate.parse(dateTime, dateFormatter);
    } catch (DateTimeParseException e1) {
      try {
        // 형식 2: yyyy-MM-dd HH:mm:ss
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsedDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
        return parsedDateTime.toLocalDate();
      } catch (DateTimeParseException e2) {
        throw new IllegalArgumentException("Invalid date format. Supported formats: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss");
      }
    }
  }

}
