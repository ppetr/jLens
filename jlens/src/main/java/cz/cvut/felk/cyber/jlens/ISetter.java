package cz.cvut.felk.cyber.jlens;

/**
 */
public interface ISetter<R,F>
    extends IGetter<R,F>
{
    public void set(R record, F fieldValue);
}
