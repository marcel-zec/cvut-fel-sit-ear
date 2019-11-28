package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends User{

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
    private Gender gender;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Accommodation> accommodations;


    public Student() {
        setRole(Role.STUDENT);
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

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Accommodation> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(List<Accommodation> accommodations) {
        this.accommodations = accommodations;
    }

    public void addAccommodation(Accommodation accommodation) {
        if (accommodations == null){
            accommodations = new ArrayList<>();
        }
        accommodations.add(accommodation);
    }

    public boolean hasActiveAccommodation(){
        return accommodations.stream().anyMatch(accommodation -> accommodation.getStatus().equals(Status.ACTIVE));
    }

    public boolean hasReservation(){
        return accommodations.stream().anyMatch(accommodation -> accommodation.getStatus().equals(Status.PENDING) || accommodation.getStatus().equals(Status.APPROVED));
    }
}
