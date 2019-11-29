package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.*;

import java.time.LocalDate;

@Entity
public class Accommodation extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDate dateStart;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDate dateEnd;

    @Basic(optional = false)
    @Column(nullable = true)
    private LocalDate dateUnusualEnd;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name="room_id", nullable=false)
    private Room room;


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

    public Room getRoom() { return room; }

    public void setRoom(Room room) { this.room = room; }

    public LocalDate getDateUnusualEnd() {

        return dateUnusualEnd;
    }


    public void setDateUnusualEnd(LocalDate dateUnusualEnd) {

        this.dateUnusualEnd = dateUnusualEnd;
    }
}
