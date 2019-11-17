package cz.cvut.fel.ear.hamrazec.dormitory.model;

public enum Status {
    //accommodation
    ACTIVE("ACTIVE"),
    CANCELED("CANCELED"),
    ENDED("ENDED"),
    //reservation
    PENDING("PENDING_APPROVAL"),
    APPROVED("RESERVATION_APPROVED"),
    RESERVATION_CANCELED("RESERVATION_CANCELED");

    private final String status;


    Status(String status) {

        this.status = status;
    }


    @Override
    public String toString() {

        return status;
    }
}
