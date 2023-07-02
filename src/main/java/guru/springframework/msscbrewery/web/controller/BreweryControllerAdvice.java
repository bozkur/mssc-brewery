package guru.springframework.msscbrewery.web.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class BreweryControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldErrorSummary>> handleValidationError(MethodArgumentNotValidException ex) {
        List<FieldErrorSummary> responseBody = ex.getFieldErrors().stream().map(FieldErrorSummary::new).collect(Collectors.toList());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @NoArgsConstructor
    @Getter
    public static final class FieldErrorSummary implements Serializable {
        private  String fieldName;
        private  String errorCode;

        private FieldErrorSummary(FieldError fieldError) {
            this.fieldName = fieldError.getField();
            this.errorCode = fieldError.getCode();
        }
    }
}
