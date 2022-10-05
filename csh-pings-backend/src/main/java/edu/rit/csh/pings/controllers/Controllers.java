package edu.rit.csh.pings.controllers;

import edu.rit.csh.pings.auth.UserAccessException;
import edu.rit.csh.pings.exchange.ErrorInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class Controllers {

    private final Log log = LogFactory.getLog("pings.controller_exception_handler");

    @ExceptionHandler(UserAccessException.class)
    private ResponseEntity<ErrorInfo> userAccessException(UserAccessException e) {
        this.log.warn("User Access Exception", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorInfo(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ErrorInfo> illegalArgumentException(IllegalArgumentException e) {
        this.log.debug("Illegal Argument Exception", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorInfo(e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    private ResponseEntity<ErrorInfo> noSuchElementException(NoSuchElementException e) {
        this.log.info("No Such Element Exception", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorInfo("Not found"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<ErrorInfo> sqlConstraintViolation(DataIntegrityViolationException e) {
        this.log.warn("SQL Constraint Violation", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorInfo("SQL Constraint violated. Probably a duplicate key?", e));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<ErrorInfo> methodNotSupported(HttpRequestMethodNotSupportedException e) {
        this.log.debug("Wrong method for endpoint", e);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorInfo("Method not Allowed"));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    private ResponseEntity<ErrorInfo> missingRequestHeader(MissingRequestHeaderException e) {
        this.log.debug("Missing Request Header: " + e.getHeaderName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorInfo("Missing Header: " + e.getHeaderName()));
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorInfo> other(Exception e) {
        this.log.warn("Other Exception reported", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorInfo(e.getMessage(), e));
    }
}
