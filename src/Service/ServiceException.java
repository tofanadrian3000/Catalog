package Service;

public class ServiceException extends RuntimeException{
    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException() {
    }
}
