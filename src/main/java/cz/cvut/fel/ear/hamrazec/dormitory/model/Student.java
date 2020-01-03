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

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Accommodation> accommodations;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Reservation> reservations;


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
        if (accommodations == null){
            accommodations = new ArrayList<>();
        }
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

    /**
     * Find out if student has active accommodation.
     * @return true when has, false otherwise
     */
    public boolean hasActiveAccommodation(){
        if (accommodations == null) return false;
        return accommodations.stream().anyMatch(accommodation -> accommodation.getStatus().equals(Status.ACC_ACTIVE));
    }

    public List<Reservation> getReservations() {
        if (reservations == null){
            reservations = new ArrayList<>();
        }
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation) {
        if (reservations == null){
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
    }

    public void cancelReservation(Reservation reservation){
        if (reservations.contains(reservation)){
            reservations.remove(reservation);
        }
    }

}
