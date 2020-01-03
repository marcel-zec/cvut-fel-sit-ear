package cz.cvut.fel.ear.hamrazec.dormitory.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "DORMITORY_USER")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false, length = 30)
    @Size(max = 30, min = 1, message = "First name is in incorrect format.")
    private String firstName;

    @Basic(optional = false)
    @Column(nullable = false)
    private String lastName;

    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    @Size(max = 255, min = 3, message = "Username is in incorrect format.")
    private String username;

    @Basic(optional = false)
    @Column(nullable = false)
    @Size(max = 255, min = 6, message = "Password is in incorrect format.")
    @JsonIgnore
    private String password;

    @Email(message = "Email should be valid")
    @Basic(optional = false)
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Basic(optional = true)
    @Column(nullable = true)
    private LocalDate deleted_at;

    public User() {
    }

    public User(@Size(max = 255, min = 3, message = "Username is in incorrect format.") String username,
                @Size(max = 255, min = 6, message = "Password is in incorrect format.") String password) {
        this.username = username;
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void erasePassword() {
        this.password = null;
    }

    public String getEmail() {
        return email;
    }

    public void delete(){
        deleted_at = LocalDate.now();
    }

    @JsonIgnore
    public boolean isNotDeleted(){
        return deleted_at == null;
    }

//    public UserRole getUserRole() {
//        return userRole;
//    }
//
//    public void setUserRole(UserRole userRole) {
//        this.userRole = userRole;
//    }
}
