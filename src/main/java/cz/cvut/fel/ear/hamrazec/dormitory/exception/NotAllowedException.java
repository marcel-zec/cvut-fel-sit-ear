package cz.cvut.fel.ear.hamrazec.dormitory.exception;

public class NotAllowedException extends Exception{
    public NotAllowedException() {
        super("Forbidden operation.");
    }
    public NotAllowedException(String message) {
        super(message);
    }
}
