package cz.cvut.fel.ear.hamrazec.dormitory.service;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.AccommodationDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.exception.NotFoundException;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Accommodation;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccommodationService {

    AccommodationDao acoDao;
    StudentDao studentDao;

    @Autowired
    public AccommodationService(AccommodationDao acoDao, StudentDao studentDao) {

        this.acoDao = acoDao;
        this.studentDao = studentDao;
    }

    public List<Accommodation> findAll() {
        return acoDao.findAll();
    }

    public Accommodation find(Long id) {
        return acoDao.find(id);
    }

    @Transactional
    public void create(Accommodation accommodation, Long idStudent) throws NotFoundException {
        //TODO - pridat izbu, kontrolovat volnost izby v tom datume
        Student student = studentDao.find(idStudent);
        if (student == null) throw new NotFoundException();

        acoDao.persist(accommodation);
        student.addAccommodation(accommodation);
        studentDao.update(student);
    }

    //TODO - create na náhodnu izbu

    //TODO - reserve na konkretnu izbu a nahodnu izbu


    @Transactional
    public void delete(Long id) throws NotFoundException {

        Accommodation accommodation = acoDao.find(id);
        if (accommodation == null) throw new NotFoundException();
        //TODO - nemazat ale zmenit status?w
        acoDao.remove(accommodation);
    }

}
