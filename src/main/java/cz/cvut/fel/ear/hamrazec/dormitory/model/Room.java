package cz.cvut.fel.ear.hamrazec.dormitory.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Room.findByBlockName", query = "SELECT r FROM Room r WHERE r.block.name = :blockname AND r.roomNumber = :roomNumber AND r.deleted_at is null")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
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
    @PositiveOrZero(message = "Capacity of room can not be negative")
    private Integer maxCapacity;

    @ManyToOne
    @JoinColumn(name="block_id", nullable=true)
    private Block block;

    @OrderBy("dateStart DESC")
    @OneToMany
    private List<Accommodation> pastAccommodations;

    @OrderBy("dateStart DESC")
    @OneToMany
    private List<Accommodation> actualAccommodations;

    @OrderBy("dateStart DESC")
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    public Block getBlock() { return block; }

    public void setBlock(Block block) { this.block = block; }

    public List<Accommodation> getActualAccommodations() {
        if (actualAccommodations == null) actualAccommodations = new ArrayList<>();
        return actualAccommodations;
    }

    public List<Accommodation> getPastAccommodations() {
        if (pastAccommodations == null) pastAccommodations = new ArrayList<>();
        return pastAccommodations;
    }


    public void addActualAccomodation(Accommodation accommodation){
        if (actualAccommodations == null) actualAccommodations = new ArrayList<>();
        if (actualAccommodations.size() < maxCapacity){
            this.actualAccommodations.add(accommodation);
        }
        //todo vyhod vynimku ak mam plnu izbu
    }

    public void addPastAccomodation(Accommodation accommodation){
        if (pastAccommodations == null) pastAccommodations = new ArrayList<>();
            this.pastAccommodations.add(accommodation);
    }

    public void cancelActualAccomodation(Accommodation accommodation){
        if (actualAccommodations.contains(accommodation)){
            actualAccommodations.remove(accommodation);
        }
    }

    public void cancelActualReservation(Reservation reservation){
        if (reservations.contains(reservation)){
            reservations.remove(reservation);
        }
    }

    public Integer getMaxCapacity() { return maxCapacity; }

    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

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


    public List<Reservation> getReservations() {
        if (reservations == null) reservations = new ArrayList<Reservation>();
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public void addReservation(Reservation reservation) {
        if (reservations == null) reservations = new ArrayList<Reservation>();
        this.reservations.add(reservation); }
}
