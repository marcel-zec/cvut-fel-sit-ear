package cz.cvut.fel.ear.hamrazec.dormitory.model;

import org.hibernate.annotations.Type;

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

    @Enumerated(EnumType.STRING)
    @Type(type = "org.thoughts.on.java.model.EnumTypePostgreSql")
    private Status status;


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
}
