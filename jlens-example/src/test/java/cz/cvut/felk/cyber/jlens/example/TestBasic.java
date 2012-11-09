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
 
import org.testng.annotations.*;
import static org.testng.Assert.*;

import static cz.cvut.felk.cyber.jlens.Lenses.*;
 
public class TestBasic {
    @Test
    public void aFastTest() {
        Employee e = new Employee();
        Person wife = new Person();
        Candidate seller = new Candidate();

        Employee_L.wife.set(e, wife);
        assertSame(Employee_L.wife.get(e), wife);

        Employee_L.wife.seller().set(e, seller);
        assertSame(Employee_L.wife.seller().get(e), seller);
        assertSame(Employee_L.wife.join(Person_L.seller).get(e), seller);
        assertSame(join(Employee_L.wife, Person_L.seller).get(e), seller);
        assertSame(Person_L.seller.get(wife), seller);

        seller.setName("John");
        assertEquals(Person_L.name.get(seller), "John");
        assertEquals(Employee_L.wife.seller().name().get(e), "John");
        assertEquals(Employee_L.wife.join(Person_L.seller.join(Person_L.name)).get(e), "John");
        assertEquals(join(join(Employee_L.wife, Person_L.seller), Person_L.name).get(e), "John");
    }
}
