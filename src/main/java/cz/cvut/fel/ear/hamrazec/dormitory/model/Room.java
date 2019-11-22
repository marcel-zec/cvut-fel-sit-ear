package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
public class Room extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    @Min(value = 1, message = "Room number should not be less than 1")
    @Max(value = 999, message = "Room number should not be greater than 999")
    private Integer roomNumber;

    @Basic(optional = false)
    @Column(nullable = false)
    @Min(value = 0, message = "Floor should not be less than 0")
    @Max(value = 20, message = "Floor should not be greater than 20")
    private Integer floor;

    @Basic(optional = false)
    @Column(nullable = false)
    @PositiveOrZero(message = "People in room can not be negative")
    private Integer capacity;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Accommodation> accommodations;

    public List<Accommodation> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(List<Accommodation> accommodations) {
        this.accommodations = accommodations;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getNumberOfPeople() {
        return capacity;
    }

    public void setNumberOfPeople(Integer capacity) {
        this.capacity = capacity;
    }
}
