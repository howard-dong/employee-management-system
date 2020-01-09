/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Howard
 */
public class TestCode {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MyHashTable h = new MyHashTable(12);
        h.addEmployee(new FullTimeEmployee(0, "", "", "", 0, 0));
        h.addEmployee(new FullTimeEmployee(12, "", "", "", 0, 0));

        h.removeEmployee(0);
        System.out.println(h.buckets[0].get(0).getEmployeeNumber());
    }

}
