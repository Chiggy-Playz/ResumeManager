package com.chiggy.resumeviewer;

import com.chiggy.resumeviewer.exception.InvalidFileType;
import com.chiggy.resumeviewer.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("WWW-Authenticate", "Basic realm=\"Upload UI\"");
        return new ResponseEntity<>("Unauthorized", headers, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(InvalidFileType.class)
    public String handleInvalidFileType() {
        return "redirect:/error-page?msg=Invalid+File+Type";
    }

}