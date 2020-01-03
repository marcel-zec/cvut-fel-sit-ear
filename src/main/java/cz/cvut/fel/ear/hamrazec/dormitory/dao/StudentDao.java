package cz.cvut.fel.ear.hamrazec.dormitory.dao;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Manager;
import cz.cvut.fel.ear.hamrazec.dormitory.model.Student;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;

@Repository
public class StudentDao extends BaseDao<Student> {
    public StudentDao(){super(Student.class);}

    @Override
    public Student find(Long id) {
        Objects.requireNonNull(id);
        Student student = em.find(type, id);
        if (student != null && student.isNotDeleted()) return student;
        else return null;
    }

}
