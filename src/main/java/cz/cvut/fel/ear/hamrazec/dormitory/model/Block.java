package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Block  extends AbstractEntity{

    @Basic(optional = false)
    @Column(nullable = false, length = 3)
    @Size(max = 3, min = 1, message = "Name of block is in incorrect format.")
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    @Size(max = 255, min = 5)
    private String adress;

    @ManyToMany
    @OrderBy("username")
    private List<Manager> managers;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    private List<Room> rooms;

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Manager> getManagers() {
        return managers;
    }

    public void setManagers(List<Manager> managers) {
        this.managers = managers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
