package cz.cvut.fel.ear.hamrazec.dormitory.service.security;

import cz.cvut.fel.ear.hamrazec.dormitory.dao.ManagerDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.StudentDao;
import cz.cvut.fel.ear.hamrazec.dormitory.dao.UserDao;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import cz.cvut.fel.ear.hamrazec.dormitory.security.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final StudentDao studentDao;
    private final ManagerDao managerDao;

    @Autowired
    public UserDetailsService(StudentDao studentDao, ManagerDao managerDao) {
        this.studentDao = studentDao;
        this.managerDao = managerDao;
    }

    @Override
    public UserDetails loadStudentByUsername(String username) throws UsernameNotFoundException {
        final User user = studentDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        return new UserDetails(user);
    }
}
