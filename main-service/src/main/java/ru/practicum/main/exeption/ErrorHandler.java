package ru.practicum.main.exeption;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.helper.DTFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {




    private String toString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorOfApi handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        log.error("[VALIDATION ERROR]: {}.", ex.getMessage(), ex);
        String asString = toString(ex);
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> String.format("Field: %s. Error: %s Value: %s.",
                        fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue()))
                .collect(Collectors.joining(" "));
        return new ErrorOfApi(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrect state of request",
                errorMessage,
                asString,
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorOfApi handleMissingServletRequestParameterException(final MissingServletRequestParameterException ex) {
        log.error("[REQUEST PARAMETER ERROR]: {}.", ex.getMessage(), ex);
        return new ErrorOfApi(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrect request",
                ex.getMessage(),
                toString(ex),
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorOfApi handle(final DataIntegrityViolationException ex) {
        log.error("[DATABASE CONSTRAINT ERROR]: {}.", ex.getMessage(), ex);
        return new ErrorOfApi(
                HttpStatus.CONFLICT.name(),
                "Internal error with state",
                ex.getMostSpecificCause().getMessage(), //Integrity constraint has been violated
                toString(ex),
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorOfApi handle(final ConstraintViolationException ex) {
        log.error("[DATABASE CONSTRAINT ERROR]: {}.", ex.getMessage(), ex);
        return new ErrorOfApi(
                HttpStatus.CONFLICT.name(),
                "Internal error with state",
                ex.getCause().getMessage(),
                toString(ex),
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorOfApi handleNotFoundException(final NotFoundException ex) {
        log.error("[NOT FOUND ERROR]: {}.", ex.getMessage(), ex);
        return new ErrorOfApi(
                HttpStatus.NOT_FOUND.name(),
                "Object is not found",
                ex.getMessage(),
                toString(ex),
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER)
        );

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorOfApi handleBadRequest(final BadRequest ex) {
        log.error("[Bad Request ERROR]: {},", ex.getCause(), ex);
        return new ErrorOfApi(
                HttpStatus.BAD_REQUEST.name(),
                "Condition is not suitable",
                ex.getMessage(),
                toString(ex),
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER)
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorOfApi handleConflictException(final IssueException ex) {
        log.error("[Conflict ERROR]: {},", ex.getCause(), ex);
        return new ErrorOfApi(
                HttpStatus.FORBIDDEN.name(),
                "Condition is not suitable",
                ex.getMessage(),
                toString(ex),
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER)
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorOfApi handleExceptionError(final Exception ex) {
        log.error("[INTERNAL SERVER ERROR]: {},", ex.getMessage(), ex);
        return new ErrorOfApi(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Internal server error",
                ex.getMessage(),
                toString(ex),
                LocalDateTime.now().format(DTFormatter.DATE_TIME_FORMATTER)
        );
    }

    @Getter
    private static class ErrorOfApi {

        private String status;

        private String reason;

        private String message;

        private String errors;

        private String timestamp;

        public ErrorOfApi(String status, String reason, String message, String errors, String timestamp) {
            this.status = status;
            this.reason = reason;
            this.message = message;
            this.errors = errors;
            this.timestamp = timestamp;
        }
    }



}
