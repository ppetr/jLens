package cz.cvut.felk.cyber.jlens;

public class GetterNotReadableException
    extends RuntimeException
{
    public GetterNotReadableException(Throwable ex) {
        super(ex);
    }
}
