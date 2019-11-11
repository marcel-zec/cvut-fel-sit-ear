package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import org.springframework.stereotype.Repository;

@Repository
public class ManagerDao extends BaseDao<Manager>{
        public ManagerDao(){super(Manager.class);}
}

