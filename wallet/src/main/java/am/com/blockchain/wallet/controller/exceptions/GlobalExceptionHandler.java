package am.com.blockchain.wallet.controller.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleWrongAddressException(NoSuchElementException ex) {
        return new ResponseEntity<>("The address does not belong to user", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NodeException.class)
    public ResponseEntity<String> handleNodeException(NodeException ex) {
        log.error("Node exception", ex);
        return new ResponseEntity<>("Node connection failure - " + ex.getMessage(), HttpStatus.FAILED_DEPENDENCY);
    }
}
