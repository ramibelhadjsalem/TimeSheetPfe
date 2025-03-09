package com.tunisys.TimeSheetPfe.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex, WebRequest request){

        return new ResponseEntity<ApiError>(
                ApiError
                    .builder()
                        .message(ex.getMessage())
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .httpcode(HttpStatus.NOT_FOUND.value())
                        .errorDate(LocalDateTime.now())
                    .build(),
                HttpStatus.NOT_FOUND
                );
    }
//    @ExceptionHandler(LowBalanceException.class)
//    public ResponseEntity handleLowBalanceException(LowBalanceException ex, WebRequest request){
//
//        return ApiResponseEntity.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//    @ExceptionHandler(QrCodeUsedException.class)
//    public ResponseEntity handleQrCodeUsedException(QrCodeUsedException ex,WebRequest request){
//        return ApiResponseEntity.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }



}
