package com.assignment.ride_hailing.controller;

import com.assignment.ride_hailing.dto.ErrorResponse;
import com.assignment.ride_hailing.exceptions.DuplicateRequestException;
import com.assignment.ride_hailing.exceptions.NotFoundException;
import com.assignment.ride_hailing.exceptions.OtpVerificationFailedException;
import com.assignment.ride_hailing.exceptions.RideNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RideNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .details(Map.of("rideId", exception.getRideId()))
                .message(exception.getMessage())
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateRequest(){
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .message("Request is in Process,Please wait some time!")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(exception = {NotFoundException.class,IllegalAccessException.class, OtpVerificationFailedException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse
                .builder()
                .message(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(errorResponse);
    }
}
