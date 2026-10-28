package com.test.number5;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> RuntimeExceptionHandler (RuntimeException e ){
        Map<String,Object> body = new HashMap<>();
        //返回值为400
        body.put("status",400);
        //返回错误提示
        body.put("message",e.getMessage());
        return ResponseEntity.status(400).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> MethodArgumentNotValidExceptionHandler (MethodArgumentNotValidException e){
        String message = e.getBindingResult().getFieldError().getDefaultMessage();

        Map<String,Object> body = new HashMap<>();
        //返回值为400
        body.put("status",400);
        //返回错误提示
        body.put("message",message);
        return ResponseEntity.status(400).body(body);

    }









}
