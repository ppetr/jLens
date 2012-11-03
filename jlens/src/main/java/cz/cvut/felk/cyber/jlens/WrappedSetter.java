package cz.cvut.felk.cyber.jlens;

public abstract class WrappedSetter<R,F,G extends IGetter<R,?>>
    extends WrappedGetter<R,F,G>
    implements ISetter<R,F>
{
    protected WrappedSetter(G getter, Class<F> fieldClass) {
        super(getter, fieldClass);
    }
}
