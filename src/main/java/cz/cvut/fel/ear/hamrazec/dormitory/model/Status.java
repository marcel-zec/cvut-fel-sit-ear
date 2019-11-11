package cz.cvut.fel.ear.hamrazec.dormitory.model;

public enum Status {
    PENDING("STATUS_PENDING"), AGREED("STATUS_AGREED");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
