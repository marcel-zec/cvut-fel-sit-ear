package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;

@Repository
public class ManagerDao extends BaseDao<Manager> {
    public ManagerDao() {
        super(Manager.class);
    }

    public Manager findByWorkerNumber(Integer workerNumber) {
        try {
            return em.createNamedQuery("Manager.findByWorkerNumber", Manager.class).setParameter("workerNumber", workerNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Manager find(Long id) {
        Objects.requireNonNull(id);
        Manager manager = em.find(type, id);
        if (manager != null && manager.isNotDeleted()) return manager;
        else return null;
    }
}

