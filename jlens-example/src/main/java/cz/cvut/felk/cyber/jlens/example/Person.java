package cz.cvut.felk.cyber.jlens.example;

import cz.cvut.felk.cyber.jlens.*;

@Lens
public class Person {
    private String name;

    public String getName()       { return this.name; }
    public void setName(String v) { this.name = v; }
}
