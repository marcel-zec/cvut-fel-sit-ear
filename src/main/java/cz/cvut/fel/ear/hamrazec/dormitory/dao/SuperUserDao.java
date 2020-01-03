package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.model.SuperUser;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class SuperUserDao extends BaseDao<SuperUser> {
    public SuperUserDao(){super(SuperUser.class);}

    @Override
    public SuperUser find(Long id) {
        Objects.requireNonNull(id);
        SuperUser superUser = em.find(type, id);
        if (superUser.isNotDeleted()) return superUser;
        else return null;
    }
}