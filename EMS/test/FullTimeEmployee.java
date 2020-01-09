

import EMS1.*;


public class FullTimeEmployee extends EmployeeInfo {
    // Attributes

    private double yearlySalary;

    // Constructors    
    public FullTimeEmployee(int eN, String fN, String lN, String g, double dR, double s) {
        super(eN, fN, lN, g, dR);
        yearlySalary = s;
    }

    public double getYearlySalary() {
        return yearlySalary;
    }

    public void setYearlySalary(double s) {
        yearlySalary = s;
    }

    public double calcAnnualGrossIncome() {
        return yearlySalary;
    }
}
