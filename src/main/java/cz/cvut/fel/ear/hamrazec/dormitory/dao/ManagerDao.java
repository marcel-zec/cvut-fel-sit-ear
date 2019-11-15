package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class ManagerDao extends BaseDao<Manager>{
        public ManagerDao(){super(Manager.class);}

        public Manager findByWorkerNumber(Integer workerNumber) {
                {
                        try {
                                return em.createNamedQuery("Manager.findByWorkerNumber", Manager.class).setParameter("workerNumber", workerNumber)
                                        .getSingleResult();
                        } catch (NoResultException e) {
                                return null;
                        }
                }
        }
}

