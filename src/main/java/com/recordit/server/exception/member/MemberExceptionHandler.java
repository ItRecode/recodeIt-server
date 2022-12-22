package com.recordit.server.exception.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.recordit.server.controller.MemberController;
import com.recordit.server.exception.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackageClasses = MemberController.class)
public class MemberExceptionHandler {

	@ExceptionHandler(NotFoundUserInfoInSessionException.class)
	public ResponseEntity<ErrorMessage> handleNotFoundUserInfoInSessionException(
			NotFoundUserInfoInSessionException exception) {
		return ResponseEntity.badRequest()
				.body(ErrorMessage.of(exception, HttpStatus.BAD_REQUEST));
	}

	@ExceptionHandler(NotMatchLoginTypeException.class)
	public ResponseEntity<ErrorMessage> handleNotMatchLoginTypeException(NotMatchLoginTypeException exception) {
		return ResponseEntity.badRequest()
				.body(ErrorMessage.of(exception, HttpStatus.BAD_REQUEST));
	}

	@ExceptionHandler(NotEnteredLoginTypeException.class)
	public ResponseEntity<ErrorMessage> handleNotEnteredLoginTypeException(NotEnteredLoginTypeException exception) {
		return ResponseEntity.badRequest()
				.body(ErrorMessage.of(exception, HttpStatus.BAD_REQUEST));
	}
}
