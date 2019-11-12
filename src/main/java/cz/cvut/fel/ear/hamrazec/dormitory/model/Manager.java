package cz.cvut.fel.ear.hamrazec.dormitory.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class Manager extends AbstractEntity {

@OneToOne
private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
