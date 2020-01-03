package cz.cvut.fel.ear.hamrazec.dormitory.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.AlreadyExistsException;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Manager.findByWorkerNumber", query = "SELECT m FROM Manager m WHERE m.workerNumber = :workerNumber")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Manager extends User {

    @Basic(optional = false)
    @Column(unique = true, nullable = false)
    private Integer workerNumber;

    @ManyToMany
    @JoinTable(
            name = "managers_blocks",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "block_id"))
    private List<Block> blocks;


    public Manager() {
        setRole(Role.MANAGER);
    }


    public Integer getWorkerNumber() { return workerNumber; }

    public void setWorkerNumber(Integer workerNumber) { this.workerNumber = workerNumber; }

    public List<Block> getBlocks() { return blocks; }

    public void setBlocks(List<Block> blocks) { this.blocks = blocks; }

    public void addBlock(Block block) throws AlreadyExistsException{
        if (blocks == null) blocks = new ArrayList<>();
        if (!blocks.contains(block)) {
            blocks.add(block);
        } else{
            throw new AlreadyExistsException();
        }
    }

    public void removeBlock(Block block){
        if (blocks != null){
            blocks.remove(block);
        }
    }
}
