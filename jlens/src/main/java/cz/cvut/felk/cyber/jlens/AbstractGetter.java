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
