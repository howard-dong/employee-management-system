package EMS1;

import java.util.ArrayList;

public class MyHashTable {

    //Attributes
    private int bucketSize;

    public ArrayList<EmployeeInfo>[] buckets;

    //Constructors    
    public MyHashTable(int num) {
        bucketSize = num;
        buckets = new ArrayList[num];
        for (int i = 0; i < num; i++) {
            buckets[i] = new ArrayList<>();
        }
    }

    //Hashing
    private void checkSize() {
        boolean overflow = false;
        for (ArrayList bucket : buckets) {
            if (bucket.size() > bucketSize) {
                overflow = true;
            }
        }
        if (overflow) {
            reHash();
        }
    }

    private void reHash() {
        bucketSize += 1;
        ArrayList<EmployeeInfo>[] tempBuckets = buckets.clone();

        buckets = new ArrayList[bucketSize];
        for (int i = 0; i < bucketSize; i++) {
            buckets[i] = new ArrayList<>();
        }

        for (ArrayList<EmployeeInfo> bucket : tempBuckets) {
            for (EmployeeInfo emp : bucket) {
                addEmployee(emp);
            }
        }
    }

    private int myHash(int EmployeeNumber) {
        return EmployeeNumber % bucketSize;
    }

    //Methods
    public int getBucketSize() {
        return bucketSize;
    }

    public void setBucketSize(int s) {
        if (s >= 0) {
            bucketSize = s;
        }
    }

    public void addEmployee(EmployeeInfo toBeAdded) { // add Employee to the hashtable
        if (searchEmployee(toBeAdded.getEmployeeNumber()) == null) { //Duplicates
            int index = myHash(toBeAdded.getEmployeeNumber());
            buckets[index].add(toBeAdded);
            checkSize();
        } else {
            System.out.println("Duplicate");
        }
    }

    public EmployeeInfo searchEmployee(int EmployeeNumber) {                    // search for an Employee and display his/her information
        for (int i = 0; i != buckets[myHash(EmployeeNumber)].size(); i++) {
            if (EmployeeNumber == buckets[myHash(EmployeeNumber)].get(i).getEmployeeNumber()) {
                return buckets[myHash(EmployeeNumber)].get(i);
            }
        }
        return null;
    }

    public EmployeeInfo removeEmployee(EmployeeInfo toBeRemoved) {
        int index = myHash(toBeRemoved.getEmployeeNumber());
        if (buckets[index] != null) {
            for (int i = 0; i < buckets[index].size(); i++) {
                if (toBeRemoved.getEmployeeNumber() == buckets[index].get(i).getEmployeeNumber()) {
                    buckets[index].remove(i);
                    return toBeRemoved;
                }
            }
        }
        return null;
    }

    public EmployeeInfo removeEmployee(int toBeRemoved) {   //remove from hashtable
        int index = myHash(toBeRemoved);
        if (buckets[index] != null) {
            for (int i = 0; i < buckets[index].size(); i++) {
                if (toBeRemoved == buckets[index].get(i).getEmployeeNumber()) {
                    EmployeeInfo removedEmployee = buckets[index].get(i);
                    buckets[index].remove(i);
                    return removedEmployee;
                }
            }
        }
        return null;
    }
    
    public ArrayList<EmployeeInfo> displayEmployees() {
        ArrayList<EmployeeInfo> employees = new ArrayList<>();
        for (int i = 0; i < bucketSize; i++) {
            for (EmployeeInfo employee : buckets[i]) {
                employees.add(employee);
            }
        }
        return employees;
    }

}
