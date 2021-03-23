package com.hesho.reservation.security;

import net.bytebuddy.implementation.bind.annotation.Super;

public class ImageException extends RuntimeException{

    public ImageException(String message) {
        super(message);
    }
    public ImageException(String message, Throwable cause) {

        super(message, cause);
    }
    public ImageException(Throwable cause) {

        super( cause);
}
}
