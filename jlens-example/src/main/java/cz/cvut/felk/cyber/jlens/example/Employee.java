/*
    This file is part of jLens.

    jLens is free software: you can redistribute it and/or modify it under the
    terms of the GNU Lesser General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version.

    jLens is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
    FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
    more details.

    You should have received a copy of the GNU Lesser General Public License
    along with jLens.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cvut.felk.cyber.jlens.example;

import cz.cvut.felk.cyber.jlens.*;

@LensProperties
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
