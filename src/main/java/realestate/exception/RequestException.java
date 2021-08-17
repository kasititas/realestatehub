package realestate.exception;

import org.springframework.http.HttpStatus;

public class RequestException extends Exception {

    private final ApplicationError applicationError;
    private final String message;

    public RequestException(ApplicationError applicationError, String message) {
        this.applicationError = applicationError;
        this.message = message;
    }

    public RequestException(ApplicationError applicationError) {
        this.applicationError = applicationError;
        this.message = applicationError.getDefaultMessage();
    }

    public HttpStatus getHttpStatus() {
        return applicationError.getHttpStatus();
    }

    public String getCode() {
        return applicationError.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
