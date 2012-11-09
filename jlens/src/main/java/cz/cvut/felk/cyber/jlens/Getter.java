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
package cz.cvut.felk.cyber.jlens;

/**
 * Represents a getter (a read-only lens) that can read something of type <code>F</code>
 * inside something of type <code>R</code>. In addition, it provides classes of
 * both <code>R</code> and <code>F</code>.
 */
public interface Getter<R,F>
{
    public F get(R target);

    public Class<R> recordClass();
    public Class<F> fieldClass();
}
