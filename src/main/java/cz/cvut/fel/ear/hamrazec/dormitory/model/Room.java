package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
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
    @PositiveOrZero(message = "Capacity of room can not be negative")
    private Integer maxCapacity;

    @ManyToOne
    @JoinColumn(name="block_id", nullable=false)
    private Block block;

    @OneToMany
    private List<Accommodation> pastAccommodations;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Accommodation> actualAccommodations;

//    @OneToOne
//    private Accommodation actualAccommodation;

//BLBOST, lebo tam byva viac ludi
//    public Accommodation getActualAccommodation() { return actualAccommodation; }
//
//    public void setActualAccommodation(Accommodation actualAccommodation) { this.actualAccommodation = actualAccommodation; }

    public Block getBlock() { return block; }

    public void setBlock(Block block) { this.block = block; }

    public List<Accommodation> getActualAccommodations() { return actualAccommodations; }

    public List<Accommodation> getPastAccommodations() { return pastAccommodations; }


//    public void setActualAccommodations(List<Accommodation> actualAccommodations) {
//
//        for (Accommodation acommodation: actualAccommodations) {
//            acommodation.
//        }
//        this.pastAccommodations = pastAccommodations.add(actualAccommodations.get());
//        this.actualAccommodations = actualAccommodations;
//   }

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

}
