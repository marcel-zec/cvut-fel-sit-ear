package cz.cvut.fel.ear.hamrazec.dormitory.model;

public enum Status {
    //accommodation
    ACC_ACTIVE("ACTIVE"),
    ACC_CANCELED("CANCELED"),
    ACC_ENDED("ENDED"),
    //reservation
    RES_PENDING("PENDING_APPROVAL"),
    RES_APPROVED("RESERVATION_APPROVED"),
    RES_CANCELED("RESERVATION_CANCELED");

    private final String status;


    Status(String status) {

        this.status = status;
    }


    @Override
    public String toString() {

        return status;
    }
}
