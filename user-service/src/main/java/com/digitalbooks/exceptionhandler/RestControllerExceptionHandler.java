package com.digitalbooks.exceptionhandler;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class RestControllerExceptionHandler {
	
	Logger log = LoggerFactory.getLogger(RestControllerExceptionHandler.class);
	
	@ResponseStatus(HttpStatus.BAD_REQUEST) 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		log.error(exception.getMessage());
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        Map<String, String> errorsMap = new HashMap<>();
         ListIterator<ObjectError> iterator = errors.listIterator();
        while(iterator.hasNext()) {
            ObjectError error = iterator.next();
            errorsMap.put(error.getCode(), error.getDefaultMessage());
        }
        return errorsMap;
    }
	
	@ResponseStatus(HttpStatus.BAD_REQUEST) 
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException exception) {
		log.error(exception.getMessage());
        return ResponseEntity.badRequest().body(exception.getMessage().split(" : ")[1]);
    }
	
	@ResponseStatus(HttpStatus.BAD_REQUEST) 
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
		log.error(exception.getMessage());
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
	
}
