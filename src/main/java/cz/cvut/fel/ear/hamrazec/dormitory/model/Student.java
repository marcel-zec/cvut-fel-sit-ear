package cz.cvut.fel.ear.hamrazec.dormitory.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
public class Student extends User {

    @Basic(optional = false)
    @Column(nullable = false)
    private String university;

    @Basic(optional = false)
    @Column(nullable = false)
    @Past
    private LocalDate birth;

    @Basic(optional = false)
    @Column(nullable = false)
    @Size(max = 26, min = 16, message = "Bank account is in incorrect format. Please use IBAN format.")
    private String bankAccountNumber;

    @Basic(optional = false)
    @Column(nullable = false)
    @FutureOrPresent
    private LocalDate endOfStudy;

    @Enumerated(EnumType.STRING)
    @Type(type = "org.thoughts.on.java.model.EnumTypePostgreSql")
    private Gender gender;

    public Student(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public LocalDate getEndOfStudy() {
        return endOfStudy;
    }

    public void setEndOfStudy(LocalDate endOfStudy) {
        this.endOfStudy = endOfStudy;
    }
}
