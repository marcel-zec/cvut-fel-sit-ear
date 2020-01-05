package cz.cvut.fel.ear.hamrazec.dormitory.exception;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;

import java.time.LocalDate;

public class EndOfStudyExpirationException extends Exception {
    public EndOfStudyExpirationException(LocalDate endOfStudy, LocalDate wantedDate) {
        super("Chosen date (" + wantedDate + ") is after end of study date (" + endOfStudy + ") of this student.");
    }
}
