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
