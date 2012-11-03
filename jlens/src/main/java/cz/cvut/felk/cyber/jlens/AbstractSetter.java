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

public abstract class AbstractSetter<R,F>
    extends AbstractGetter<R,F>
    implements ISetter<R,F>
{
    protected AbstractSetter(Class<R> recordClass, Class<F> fieldClass) {
        super(recordClass, fieldClass);
    }

    // .................................................................

    public <M> ISetter<M,F> coerce(Class<M> clazz) {
        return IGetters.coerce(clazz, this);
    }
    public <M> ISetter<M,F> coerceFrom(IGetter<M,?> first) {
        return new IGetters.Coerce<M,F,R>(first, this);
    }
}
