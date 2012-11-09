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
