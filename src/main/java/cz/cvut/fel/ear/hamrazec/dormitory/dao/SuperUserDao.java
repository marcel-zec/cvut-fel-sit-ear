package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import cz.cvut.fel.ear.hamrazec.dormitory.model.SuperUser;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;

@Repository
public class SuperUserDao extends BaseDao<SuperUser> {
    public SuperUserDao(){super(SuperUser.class);}

}