package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Objects;

@Repository
public class AccommodationDao extends BaseDao<Accommodation> {
    public AccommodationDao(){super(Accommodation.class);}

    @Override
    public Accommodation find(Long id) {
        Objects.requireNonNull(id);
        Accommodation a = em.find(type, id);
        if (a != null && a.isNotDeleted()) return a;
        return null;
    }
}
