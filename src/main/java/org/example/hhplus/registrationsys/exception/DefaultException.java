package org.example.hhplus.registrationsys.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class DefaultException extends RuntimeException  {
  private final HttpStatus httpStatus;

  public DefaultException(HttpStatus httpStatus, String message){
    super(message);
    this.httpStatus = httpStatus;
  }

}
