package net.eric_nicolas.sword.mechanism;

/**
 * TAtom - Base class for all S.W.O.R.D objects.
 * Implements the linked tree structure for organizing objects in parent-child hierarchies.
 */
public class TAtom {

    protected TAtom _Next;
    protected TAtom _Previous;
    protected TAtom _Son;
    protected TAtom _Father;
    protected long register;
    protected long ident;

    public TAtom() {
        this._Next = null;
        this._Previous = null;
        this._Son = null;
        this._Father = null;
        this.register = 0;
        this.ident = 0;
    }

    public void insertIn(TAtom father) {
        if (father == null) {
            return;
        }

        this._Father = father;

        if (father._Son == null) {
            father._Son = this;
            this._Next = null;
            this._Previous = null;
        } else {
            TAtom last = father._Son;
            while (last._Next != null) {
                last = last._Next;
            }
            last._Next = this;
            this._Previous = last;
            this._Next = null;
        }
    }

    public void remove() {
        if (_Father != null) {
            if (_Father._Son == this) {
                _Father._Son = _Next;
            }
        }

        if (_Previous != null) {
            _Previous._Next = _Next;
        }
        if (_Next != null) {
            _Next._Previous = _Previous;
        }

        _Father = null;
        _Next = null;
        _Previous = null;
    }

    /**
     * Move this object to the end of its sibling list (brings to front for z-order).
     */
    public void bringToFront() {
        if (_Father == null || _Next == null) {
            return; // Already at end or no parent
        }

        // Remove from current position
        if (_Previous != null) {
            _Previous._Next = _Next;
        } else {
            // We're the first child
            _Father._Son = _Next;
        }
        if (_Next != null) {
            _Next._Previous = _Previous;
        }

        // Find last sibling
        TAtom last = _Father._Son;
        while (last._Next != null) {
            last = last._Next;
        }

        // Insert at end
        last._Next = this;
        this._Previous = last;
        this._Next = null;
    }

    public TAtom next() {
        return _Next;
    }

    public TAtom previous() {
        return _Previous;
    }

    public TAtom son() {
        return _Son;
    }

    public TAtom father() {
        return _Father;
    }

    /**
     * Get the last sibling in the list.
     */
    public TAtom last() {
        TAtom current = this;
        while (current._Next != null) {
            current = current._Next;
        }
        return current;
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
