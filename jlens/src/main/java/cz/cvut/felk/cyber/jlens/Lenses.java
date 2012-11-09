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

/**
 * A set of helper classes and methods for creating new lenses from existing
 * ones.
 */
public final class Lenses
{
    private Lenses() {
        throw new UnsupportedOperationException();
    }


    /**
     * Composes two getters (read-only lenses).
     * @see Lenses.JoinGetter
     */
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
    /**
     * Same as {@link join(Getter,Getter)}, but allows <code>null</code> values.
     * If one of the arguments is <code>null</code>, the result is also <code>null</code>.
     */
    public static <U,T,X> Getter<U,X> joinOrNull(
            Getter<U,T> first,
            Getter<? super T,X> second)
    {
        if ((first == null) || (second == null))
            return null;
        return new JoinGetter<U,T,X,Getter<? super T,X>>(first, second);
    }

    /**
     * Composes a getter (read-only lens) and another lens.
     * @see Lenses.JoinLens
     */
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
    /**
     * Same as {@link join(Getter,Getter)}, but allows <code>null</code> values.
     * If one of the arguments is <code>null</code>, the result is also <code>null</code>.
     */
    public static <U,T,X> Lens<U,X> joinOrNull(
            Getter<U,T> first,
            final Lens<? super T,X> second)
    {
        if ((first == null) || (second == null))
            return null;
        return new JoinLens<U,T,X,Lens<? super T,X>>(first, second);
    }

    /**
     * An implementation of composing two {@link Getter getters}.
     */
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
    /**
     * The implementation of composing a {@link Getter getter} and
     * a {@link Lens lens}.
     */
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
    public static <M,T> Lens<M,Option<T>> opt(Lens<M,T> lens) {
        if (lens == null)
            throw new NullPointerException("The Lens is null");
        return new Opt<M,T>(lens);
    }
    public static <M,T> Lens<M,Option<T>> optOrNull(Lens<M,T> lens) {
        if (lens == null)
            return null;
        return new Opt<M,T>(lens);
    }

    public static class Opt<M,T>
            extends WrappedLens<M,Option<T>,Lens<M,T>>
        {
            public Opt(Lens<M,T> lens) {
                super(lens);
            }

            @Override public Option<T> get(M target) {
                return Options.fromNull(getter.get(target));
            }
            @Override public void set(M target, Option<T> value) {
                getter.set(target, Options.toNull(value));
            }
        }
    */


    /**
     * Converts a lens into another that operates on a different class.
     * @see Lenses.Coerce
     */
    public static <X,M,T> Lens<M,T> coerce(Class<M> clazz, Lens<X,T> lens) {
        if (clazz == null)
            throw new NullPointerException("The class is null");
        if (lens == null)
            throw new NullPointerException("The Lens is null");
        return new Coerce<M,T,X>(clazz, lens);
    }
    /**
     * Same as {@link coerce(Class,Lens)}, but allows <code>null</code> values.
     * If one of the arguments is <code>null</code>, the result is also <code>null</code>.
     */
    public static <M,T,X> Lens<M,T> coerceOrNull(Class<M> clazz, Lens<X,T> lens) {
        if ((clazz == null) || (lens == null))
            return null;
        return new Coerce<M,T,X>(clazz, lens);
    }

    /**
     * The implementation of coercing a {@link Lens lens} to operate on a
     * different class. During every operation the returned lens checks if the
     * target object is a subclass of <var>X</var>. If it is, the operation
     * is forwarded to the underlying lens. Otherwise a new {@link
     * GetterNotReadableException} is thrown.
     */
    public static class Coerce<M,T,X>
            extends WrappedLens<M,T,Getter<M,?>>
        {
            private final Lens<X,T> second;
            public Coerce(Getter<M,?> first, Lens<X,T> lens) {
                super(first, lens.fieldClass());
                this.second = lens;
            }
            public Coerce(Class<M> clazz, Lens<X,T> lens) {
                this(new Identity<M>(clazz), lens);
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

            /**
             * Returns <code>true</code> if its possible to read the target
             * with this lens.
             */
            public boolean isReadable(M target) {
                return second.recordClass().isInstance(getter.get(target));
            }
        }


    /**
     * An <a href="https://en.wikipedia.org/wiki/Identity_function">identity</a>
     * getter that returns the object passed to {@link Lenses.Identity#get get}.
     */
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
