package cz.cvut.felk.cyber.jlens.example;

import cz.cvut.felk.cyber.jlens.*;

@Lens
public class Employee
    extends Person
{
    private int officeNo;
    private Employee manager;

    public int getOfficeNo()       { return this.officeNo; }
    public void setOfficeNo(int v) { this.officeNo = v; }

    public Employee getManager()        { return this.manager; }
    public void setManager(Employee v)  { this.manager = v; }
}
