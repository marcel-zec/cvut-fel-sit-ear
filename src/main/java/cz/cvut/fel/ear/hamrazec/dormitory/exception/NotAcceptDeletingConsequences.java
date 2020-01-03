package cz.cvut.fel.ear.hamrazec.dormitory.exception;

public class NotAcceptDeletingConsequences extends Exception{
    public NotAcceptDeletingConsequences() {
        super("You did not accept possible consequences by deleting. For accept send request with parameter accept=true.");
    }
}
