package com.tachnostack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tachnostack.domain.Bank;
import com.tachnostack.exception.NotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Bank> handleNotFound(){
		System.out.println("handleNotFound");
		System.out.println("handleNotFoun- "+ Thread.currentThread().getName());		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}
}
