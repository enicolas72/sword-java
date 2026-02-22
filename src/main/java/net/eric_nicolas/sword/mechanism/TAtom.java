package net.eric_nicolas.sword.mechanism;

/**
 * TAtom - Base class for all S.W.O.R.D objects.
 * Maintains a parent reference (_Father) for coordinate and ownership chains.
 */
public class TAtom {

    protected TAtom _Father;
    protected long register;
    protected long ident;

    public TAtom() {
        this._Father = null;
        this.register = 0;
        this.ident = 0;
    }

    public TAtom father() {
        return _Father;
    }

    public long getRegister() {
        return register;
    }

    public void setRegister(long register) {
        this.register = register;
    }

    public long getIdent() {
        return ident;
    }

    public void setIdent(long ident) {
        this.ident = ident;
    }

    public TAtom duplicate() {
        TAtom copy = new TAtom();
        copy.register = this.register;
        copy.ident = this.ident;
        return copy;
    }
}
