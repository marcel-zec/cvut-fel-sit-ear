package cz.cvut.fel.ear.hamrazec.dormitory.service.security;

import cz.cvut.fel.ear.hamrazec.dormitory.model.Role;
import cz.cvut.fel.ear.hamrazec.dormitory.model.User;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    public boolean isStudent(User user){
        return user.getRole() == Role.STUDENT;
    }

    public boolean isManager(User user){
        return user.getRole() == Role.MANAGER || user.getRole() == Role.SUPERUSER;
    }
}
