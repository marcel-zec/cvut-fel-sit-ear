package cz.cvut.fel.ear.hamrazec.dormitory.model;

public enum Role {
    STUDENT("student"), MANAGER("manager"),SUPERUSER("superuser");

    private final String role;

    Role(String role) {
        this.role = role;
    }


    @Override
    public String toString() {
        return role;
    }
}

