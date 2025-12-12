package OpenDataPhilly.common;

public class ParkingViolation {
    private String timestamp;
    private double fine;
    private String description;
    private String vehicleID;
    private String state;
    private String violationID;
    private String zipCode;

    // constructor
    public ParkingViolation(String timestamp, double fine, String description,
                            String vehicleID, String state, String violationID,
                            String zipCode) {
        this.timestamp = timestamp;
        this.fine = fine;
        this.description = description;
        this.vehicleID = vehicleID;
        this.state = state;
        this.violationID = violationID;
        this.zipCode = zipCode;
    }

    // accessors
    public String getTimestamp() { return timestamp; }

    public double getFine() { return fine; }

    public String getDescription() { return description; }

    public String getVehicleID() { return vehicleID; }

    public String getState() { return state; }

    public String getViolationID() { return violationID; }

    public String getZipCode() { return zipCode; }
}