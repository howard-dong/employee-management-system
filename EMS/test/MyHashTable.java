

import java.util.ArrayList;

public class MyHashTable {

    //Attributes
    private int bucketSize;

    ArrayList<EmployeeInfo>[] buckets;

    //Constructors    
    public MyHashTable(int num) {
        bucketSize = num;
        buckets = new ArrayList[num];
        for (int i = 0; i < num; i++) {
            buckets[i] = new ArrayList<>();
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
        int index = myHash(toBeAdded.getEmployeeNumber());
        buckets[index].add(toBeAdded);

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

}
