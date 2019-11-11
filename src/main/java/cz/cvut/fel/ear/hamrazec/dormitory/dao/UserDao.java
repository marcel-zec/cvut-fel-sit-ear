package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BaseDao<User> {
    public UserDao(){super(User.class);}
}