package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Block.findByName", query = "SELECT b FROM Block b WHERE b.name = :blockname")
})
public class Block  extends AbstractEntity{

    @Basic(optional = false)
    @Column(nullable = false, length = 3, unique = true)
    @Size(max = 3, min = 1, message = "Name of block is in incorrect format.")
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    @Size(max = 255, min = 5)
    private String address;

    @ManyToMany(mappedBy = "blocks")
    private List<Manager> managers;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<Room> rooms;

    public Block(@Size(max = 3, min = 1, message = "Name of block is in incorrect format.") String name, @Size(max = 255, min = 5) String address) {

        this.name = name;
        this.address = address;
    }


    public Block() {

    }


    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Manager> getManagers() {
        if (managers == null) managers = new ArrayList<>();
        return managers;
    }

    public void setManagers(List<Manager> managers) {
        this.managers = managers;
    }

    public void addManager(Manager manager) {
        if (this.managers == null) this.managers = new ArrayList<>();
        this.managers.add(manager);
    }

    public void removeManager(Manager manager){
        if (this.managers != null){
            this.managers.remove(manager);
        }
    }

    public void addRoom(Room room){

        if (this.rooms == null) this.rooms = new ArrayList<>();
        this.rooms.add(room);
    }

    public void removeRoom(Room room){
        if (this.rooms != null){
            this.rooms.remove(room);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
