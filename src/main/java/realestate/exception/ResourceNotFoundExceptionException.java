package realestate.exception;

public class ResourceNotFoundExceptionException extends RequestException {
    public ResourceNotFoundExceptionException() {
        super(ApplicationError.NOT_FOUND);
    }
}
