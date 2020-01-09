package EMS1;

public class PartTimeEmployee extends EmployeeInfo {

    private double hourlyWage;
    private double hoursPerWeek;
    private double weeksPerYear;

    public PartTimeEmployee(int eN, String fN, String lN, String g, double dR, String l, double hW, double hPW, double wPY) {
        super(eN, fN, lN, g, dR, l);
        hourlyWage = hW;
        hoursPerWeek = hPW;
        weeksPerYear = wPY;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public double getHoursPerWeek() {
        return hoursPerWeek;
    }

    public double getWeeksPerYear() {
        return weeksPerYear;
    }

    public void setHourlywage(double hW) {
        hourlyWage = hW;
    }

    public void setHoursPerWeek(double hPW) {
        hoursPerWeek = hPW;
    }

    public void setWeeksPerYear(double wPY) {
        weeksPerYear = wPY;
    }

    public double calcAnnualGrossIncome() {
        return (weeksPerYear * hoursPerWeek * hourlyWage) * (1 - deductionRate);
    }
}
