package com.hesho.reservation.security;

public class ReservationException extends RuntimeException{
    public ReservationException(String message) {
        super(message);
    }
    public ReservationException(String message, Throwable cause) {

        super(message, cause);
    }
    public ReservationException(Throwable cause) {

        super( cause);
    }
}
