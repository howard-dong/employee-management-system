package EMS1;

public class EmployeeInfo {
    // Attributes

    protected int employeeNumber;
    protected String firstName;
    protected String lastName;
    protected double deductionRate;
    protected String gender;
    protected String location;

    // Construcors
    public EmployeeInfo(int eN, String fN, String lN, String g, double dR, String l) {
        firstName = fN;
        lastName = lN;
        employeeNumber = eN;
        deductionRate = dR;
        gender = g;
        location = l;
    }

    public void setLocation(String l) {
        this.location = l;
    }

    public String getLocation() {
        return location;
    }

    public int getEmployeeNumber() {
        return employeeNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getDeductionRate() {
        return deductionRate;
    }
    
    public String getGender() {
        return gender;
    }

    public void setEmployeeNumber(int s) {
        employeeNumber = s;
    }

    public void setFirstName(String fN) {
        firstName = fN;
    }

    public void setLastName(String lN) {
        lastName = lN;
    }

    public void setDeductionRate(double dR) {
        deductionRate = dR;
    }
    
    public void setGender(String g) {
        gender = g;
    }
}
