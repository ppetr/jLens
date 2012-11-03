package cz.cvut.felk.cyber.jlens;

import java.util.*;

public final class IGetters
{
    private IGetters() {
        throw new UnsupportedOperationException();
    }


    public static <U,T,X> IGetter<U,X> join(
            IGetter<U,T> first,
            IGetter<? super T,X> second)
    {
        if (first == null)
            throw new NullPointerException("First IGetter is null");
        if (second == null)
            throw new NullPointerException("Second IGetter is null");
        return new JoinGetter<U,T,X,IGetter<? super T,X>>(first, second);
    }
    public static <U,T,X> IGetter<U,X> joinOrNull(
            IGetter<U,T> first,
            IGetter<? super T,X> second)
    {
        if ((first == null) || (second == null))
            return null;
        return new JoinGetter<U,T,X,IGetter<? super T,X>>(first, second);
    }

    public static <U,T,X> ISetter<U,X> join(
            IGetter<U,T> first,
            final ISetter<? super T,X> second)
    {
        if (first == null)
            throw new NullPointerException("First IGetter is null");
        if (second == null)
            throw new NullPointerException("Second ISetter is null");
        return new JoinSetter<U,T,X,ISetter<? super T,X>>(first, second);
    }
    public static <U,T,X> ISetter<U,X> joinOrNull(
            IGetter<U,T> first,
            final ISetter<? super T,X> second)
    {
        if ((first == null) || (second == null))
            return null;
        return new JoinSetter<U,T,X,ISetter<? super T,X>>(first, second);
    }

    public static class JoinGetter<U,T,X,G extends IGetter<? super T,X>>
            extends WrappedGetter<U,X,IGetter<U,T>>
        {
            protected final G second;
            public JoinGetter(IGetter<U,T> first, G second) {
                super(first, second.fieldClass());
                this.second = second;
            }
            @Override public X get(U target) {
                return second.get(getter.get(target));
            }
        };
    public static class JoinSetter<U,T,X,G extends ISetter<? super T,X>>
            extends JoinGetter<U,T,X,G>
            implements ISetter<U,X>
        {
            public JoinSetter(IGetter<U,T> first, G second) {
                super(first, second);
            }
            @Override public void set(U target, X value) {
                second.set(getter.get(target), value);
            }
        };


    /*
    public static <M,T> ISetter<M,Option<T>> opt(ISetter<M,T> isg) {
        if (isg == null)
            throw new NullPointerException("The ISetter is null");
        return new Opt<M,T>(isg);
    }
    public static <M,T> ISetter<M,Option<T>> optOrNull(ISetter<M,T> isg) {
        if (isg == null)
            return null;
        return new Opt<M,T>(isg);
    }

    public static class Opt<M,T>
            extends WrappedSetter<M,Option<T>,ISetter<M,T>>
        {
            public Opt(ISetter<M,T> isg) {
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


    public static <M,T,X> ISetter<M,T> coerce(Class<M> clazz, ISetter<X,T> isg) {
        if (clazz == null)
            throw new NullPointerException("The class is null");
        if (isg == null)
            throw new NullPointerException("The ISetter is null");
        return new Coerce<M,T,X>(clazz, isg);
    }
    public static <M,T,X> ISetter<M,T> coerceOrNull(Class<M> clazz, ISetter<X,T> isg) {
        if ((clazz == null) || (isg == null))
            return null;
        return new Coerce<M,T,X>(clazz, isg);
    }

    public static class Coerce<M,T,X>
            extends WrappedSetter<M,T,IGetter<M,?>>
        {
            private final ISetter<X,T> second;
            public Coerce(IGetter<M,?> first, ISetter<X,T> isg) {
                super(first, isg.fieldClass());
                this.second = isg;
            }
            public Coerce(Class<M> clazz, ISetter<X,T> isg) {
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
