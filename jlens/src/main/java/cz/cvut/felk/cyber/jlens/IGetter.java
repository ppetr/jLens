package cz.cvut.felk.cyber.jlens;

/**
 */
public interface IGetter<R,F>
{
    public F get(R target);

    public Class<R> recordClass();
    public Class<F> fieldClass();
}
