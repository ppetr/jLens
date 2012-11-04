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

import java.util.*;

public final class Lenses
{
    private Lenses() {
        throw new UnsupportedOperationException();
    }


    public static <U,T,X> Getter<U,X> join(
            Getter<U,T> first,
            Getter<? super T,X> second)
    {
        if (first == null)
            throw new NullPointerException("First Getter is null");
        if (second == null)
            throw new NullPointerException("Second Getter is null");
        return new JoinGetter<U,T,X,Getter<? super T,X>>(first, second);
    }
    public static <U,T,X> Getter<U,X> joinOrNull(
            Getter<U,T> first,
            Getter<? super T,X> second)
    {
        if ((first == null) || (second == null))
            return null;
        return new JoinGetter<U,T,X,Getter<? super T,X>>(first, second);
    }

    public static <U,T,X> Lens<U,X> join(
            Getter<U,T> first,
            final Lens<? super T,X> second)
    {
        if (first == null)
            throw new NullPointerException("First Getter is null");
        if (second == null)
            throw new NullPointerException("Second Lens is null");
        return new JoinLens<U,T,X,Lens<? super T,X>>(first, second);
    }
    public static <U,T,X> Lens<U,X> joinOrNull(
            Getter<U,T> first,
            final Lens<? super T,X> second)
    {
        if ((first == null) || (second == null))
            return null;
        return new JoinLens<U,T,X,Lens<? super T,X>>(first, second);
    }

    public static class JoinGetter<U,T,X,G extends Getter<? super T,X>>
            extends WrappedGetter<U,X,Getter<U,T>>
        {
            protected final G second;
            public JoinGetter(Getter<U,T> first, G second) {
                super(first, second.fieldClass());
                this.second = second;
            }
            @Override public X get(U target) {
                return second.get(getter.get(target));
            }
        };
    public static class JoinLens<U,T,X,G extends Lens<? super T,X>>
            extends JoinGetter<U,T,X,G>
            implements Lens<U,X>
        {
            public JoinLens(Getter<U,T> first, G second) {
                super(first, second);
            }
            @Override public void set(U target, X value) {
                second.set(getter.get(target), value);
            }
        };


    /*
    public static <M,T> Lens<M,Option<T>> opt(Lens<M,T> isg) {
        if (isg == null)
            throw new NullPointerException("The Lens is null");
        return new Opt<M,T>(isg);
    }
    public static <M,T> Lens<M,Option<T>> optOrNull(Lens<M,T> isg) {
        if (isg == null)
            return null;
        return new Opt<M,T>(isg);
    }

    public static class Opt<M,T>
            extends WrappedLens<M,Option<T>,Lens<M,T>>
        {
            public Opt(Lens<M,T> isg) {
                super(isg);
            }

            @Override public Option<T> get(M target) {
                return Options.fromNull(getter.get(target));
            }
            @Override public void set(M target, Option<T> value) {
                getter.set(target, Options.toNull(value));
            }
        }
    */


    public static <M,T,X> Lens<M,T> coerce(Class<M> clazz, Lens<X,T> isg) {
        if (clazz == null)
            throw new NullPointerException("The class is null");
        if (isg == null)
            throw new NullPointerException("The Lens is null");
        return new Coerce<M,T,X>(clazz, isg);
    }
    public static <M,T,X> Lens<M,T> coerceOrNull(Class<M> clazz, Lens<X,T> isg) {
        if ((clazz == null) || (isg == null))
            return null;
        return new Coerce<M,T,X>(clazz, isg);
    }

    public static class Coerce<M,T,X>
            extends WrappedLens<M,T,Getter<M,?>>
        {
            private final Lens<X,T> second;
            public Coerce(Getter<M,?> first, Lens<X,T> isg) {
                super(first, isg.fieldClass());
                this.second = isg;
            }
            public Coerce(Class<M> clazz, Lens<X,T> isg) {
                this(new Identity<M>(clazz), isg);
            }

            private final X cast(Object target) {
                return second.recordClass().cast(target);
            }
            private final X castM(M target) {
                try {
                    return second.recordClass().cast(getter.get(target));
                } catch (ClassCastException ex) {
                    throw new GetterNotReadableException(ex);
                }
            }
            @Override public T get(M target) {
                return second.get(castM(target));
            }
            @Override public void set(M target, T value) {
                second.set(castM(target), value);
            }

            public boolean isReadable(M target) {
                return second.recordClass().isInstance(getter.get(target));
            }
        }


    public static final class Identity<R>
            extends AbstractGetter<R,R>
        {
            public Identity(Class<R> recordClass) {
                super(recordClass, recordClass);
            }
            public R get(R target) {
                return target;
            }
        }
}
