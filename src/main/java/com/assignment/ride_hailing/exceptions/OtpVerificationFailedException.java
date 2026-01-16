package com.assignment.ride_hailing.exceptions;

public class OtpVerificationFailedException extends RuntimeException{
    public OtpVerificationFailedException(String message) {
        super(message);
    }
}
