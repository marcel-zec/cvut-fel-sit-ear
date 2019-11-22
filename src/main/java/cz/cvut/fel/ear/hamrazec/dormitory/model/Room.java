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

    @ManyToOne
    @JoinColumn(name="block_id", nullable=false)
    private Block block;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Accommodation> accommodations;

    @OneToOne
    private Accommodation actualAccommodation;


    public Accommodation getActualAccommodation() { return actualAccommodation; }

    public void setActualAccommodation(Accommodation actualAccommodation) { this.actualAccommodation = actualAccommodation; }

    public Block getBlock() { return block; }

    public void setBlock(Block block) { this.block = block; }

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
