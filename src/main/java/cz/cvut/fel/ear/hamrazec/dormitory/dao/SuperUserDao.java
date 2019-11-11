package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.SuperUser;
import org.springframework.stereotype.Repository;

@Repository
public class SuperUserDao extends BaseDao<SuperUser> {
    public SuperUserDao(){super(SuperUser.class);}
}