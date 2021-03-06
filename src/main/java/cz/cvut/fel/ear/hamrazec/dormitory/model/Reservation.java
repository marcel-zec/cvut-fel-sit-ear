package cz.cvut.fel.ear.hamrazec.dormitory.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Reservation extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDate dateStart;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDate dateEnd;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Basic(optional = true)
    @Column(nullable = true)
    private LocalDate dateUnusualEnd;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @OneToOne()
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;


    public LocalDate getDateStart() {

        return dateStart;
    }


    public void setDateStart(LocalDate dateStart) {

        this.dateStart = dateStart;
    }


    public LocalDate getDateEnd() {

        return dateEnd;
    }


    public void setDateEnd(LocalDate dateEnd) {

        this.dateEnd = dateEnd;
    }


    public Status getStatus() {

        return status;
    }


    public void setStatus(Status status) {

        this.status = status;
    }


    public Room getRoom() {

        return room;
    }


    public void setRoom(Room room) {

        this.room = room;
    }

    public Student getStudent() { return student; }

    public void setStudent(Student student) { this.student = student; }

    public LocalDate getDateUnusualEnd() { return dateUnusualEnd; }

    public void setDateUnusualEnd(LocalDate dateUnusualEnd) { this.dateUnusualEnd = dateUnusualEnd; }
}
