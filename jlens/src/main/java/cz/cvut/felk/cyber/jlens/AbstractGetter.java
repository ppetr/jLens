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

public abstract class AbstractGetter<R,F>
    implements IGetter<R,F>
{
    private final Class<R> recordClass;
    private final Class<F> fieldClass;

    protected AbstractGetter(Class<R> recordClass, Class<F> fieldClass) {
        this.recordClass = recordClass;
        this.fieldClass = fieldClass;
    }

    @Override
    public final Class<R> recordClass() {
        return recordClass;
    }

    @Override
    public final Class<F> fieldClass() {
        return fieldClass;
    }


    // .................................................................

    public <X> IGetter<R,X> join(IGetter<? super F,X> second) {
        return IGetters.join(this, second);
    }

    public <X> ISetter<R,X> join(ISetter<? super F,X> second) {
        return IGetters.join(this, second);
    }
}
