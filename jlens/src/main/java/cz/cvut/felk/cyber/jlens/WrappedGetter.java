package cz.cvut.felk.cyber.jlens;

public abstract class WrappedGetter<T,R,G extends IGetter<T,?>>
    extends AbstractGetter<T,R>
{
    protected final G getter;

    protected WrappedGetter(G getter, Class<R> recordClass) {
        super(getter.recordClass(), recordClass);
        this.getter = getter;
    }
}
