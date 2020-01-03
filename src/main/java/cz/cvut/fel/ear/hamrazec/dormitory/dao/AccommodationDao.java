package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;

@Repository
public class AccommodationDao extends BaseDao<Accommodation> {
    public AccommodationDao(){super(Accommodation.class);}
}
