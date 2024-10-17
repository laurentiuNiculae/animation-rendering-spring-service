package com.lniculae.animation_rendered_spring.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.lniculae.animation_rendered_spring.dto.VideoRenderingStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VideoNotFound.class)  // Handle your custom exceptions here
    public ResponseEntity<VideoRenderingStatus> handleSpecificException(VideoNotFound ex, WebRequest request) {
        return new ResponseEntity<>(new VideoRenderingStatus(ex.videoStatus, ex.message), HttpStatus.NOT_FOUND);
    }
}