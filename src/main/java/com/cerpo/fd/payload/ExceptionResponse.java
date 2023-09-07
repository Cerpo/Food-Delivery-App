package com.cerpo.fd.payload;

import org.springframework.http.HttpStatus;
import java.util.Date;

public record ExceptionResponse(HttpStatus httpStatus, String message, Date issuedAt) {
}
