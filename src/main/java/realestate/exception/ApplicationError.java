package realestate.exception;

import org.springframework.http.HttpStatus;

public enum ApplicationError {
    BAD_REQUEST("Bad request"),
    NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ApplicationError(String defaultMessage) {
        this.code = this.name();
        this.defaultMessage = defaultMessage;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    ApplicationError(String defaultMessage, HttpStatus httpStatus) {
        this.code = this.name();
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
