package cz.cvut.fel.ear.hamrazec.dormitory.exception;

public class AlreadyLoginException extends Exception {
    public AlreadyLoginException() {
        super("You are already login.");
    }
}
