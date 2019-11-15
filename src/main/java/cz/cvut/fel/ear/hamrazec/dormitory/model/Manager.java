package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Manager extends User {

    @Basic(optional = false)
    @Column(nullable = false)
    private Integer workerNumber;

    @ManyToMany
    @JoinTable(
            name = "blocks_manage",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "blocks_id"))
    private List<Block> blocks;


    public Integer getWorkerNumber() {

        return workerNumber;
    }


    public void setWorkerNumber(Integer workerNumber) {

        this.workerNumber = workerNumber;
    }


    public List<Block> getBlocks() {

        return blocks;
    }


    public void setBlocks(List<Block> blocks) {

        this.blocks = blocks;
    }
}
