package cz.cvut.fel.ear.hamrazec.dormitory.model;

public enum Gender {
    WOMAN("GENDER_WOMAN"), MAN("GENDER_MAN");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return gender;
    }
}

