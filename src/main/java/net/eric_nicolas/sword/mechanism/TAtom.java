package net.eric_nicolas.sword.mechanism;

/**
 * TAtom - Base class for all S.W.O.R.D objects.
 * Maintains a parent reference (_Father) for coordinate and ownership chains.
 */
public class TAtom {

    protected TAtom _Father;

    public TAtom() {
        this._Father = null;
    }

    public TAtom father() {
        return _Father;
    }
}
