package cz.cvut.fel.ear.hamrazec.dormitory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = true)
    @Column(nullable = true)
    private LocalDate deleted_at;

    public Long getId() {
        return id;
    }

    public void softDelete(){
        deleted_at = LocalDate.now();
    }

    @JsonIgnore
    public boolean isNotDeleted(){
        return deleted_at == null;
    }

}
