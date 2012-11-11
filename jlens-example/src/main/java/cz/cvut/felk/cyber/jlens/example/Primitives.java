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
public class Primitives {
    private byte f_byte;
    private short f_short;
    private int f_int;
    private long f_long;
    private float f_float;
    private double f_double;
    private boolean f_boolean;
    private char f_char;

    public byte getFByte()       { return this.f_byte; }
    public void setFByte(byte v) { this.f_byte = v; }

    public short getFShort()       { return this.f_short; }
    public void setFShort(short v) { this.f_short = v; }

    public int getFInt()       { return this.f_int; }
    public void setFInt(int v) { this.f_int = v; }

    public long getFLong()       { return this.f_long; }
    public void setFLong(long v) { this.f_long = v; }

    public float getFFloat()       { return this.f_float; }
    public void setFFloat(float v) { this.f_float = v; }

    public double getFDouble()       { return this.f_double; }
    public void setFDouble(double v) { this.f_double = v; }

    public boolean getFBoolean()       { return this.f_boolean; }
    public void setFBoolean(boolean v) { this.f_boolean = v; }

    public char getFChar()       { return this.f_char; }
    public void setFChar(char v) { this.f_char = v; }
}
