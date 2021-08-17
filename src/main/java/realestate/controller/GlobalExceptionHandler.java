package realestate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import realestate.exception.ApplicationError;
import realestate.exception.ErrorResponse;
import realestate.exception.RequestException;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(RequestException requestException) {

        if (requestException.getHttpStatus().is4xxClientError()) {
            logger.info("httpStatus={}, errorCode={}, errorMessage={}",
                    requestException.getHttpStatus(),
                    requestException.getCode(),
                    requestException.getMessage());
        } else {
            logger.error("httpStatus={}, errorCode={}, errorMessage={}",
                    requestException.getHttpStatus(),
                    requestException.getCode(),
                    requestException.getMessage());
            logger.error(requestException.getMessage(), requestException);
        }

        ErrorResponse errorResponse = new ErrorResponse(requestException.getCode(),
                requestException.getMessage());

        return new ResponseEntity<>(errorResponse, requestException.getHttpStatus());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.info(e.getMessage());
        String message = e.getMessage();
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        if (objectError instanceof FieldError) {
            FieldError fieldError = (FieldError) objectError;
            message = fieldError.getField() + " " + fieldError.getDefaultMessage();
        }
        return badRequestExceptionHandler(
                new RequestException(ApplicationError.BAD_REQUEST, message)
        );
    }

}
