package eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.deque;
/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public abstract class DequeItem<T extends DequeItem> {
    
    private T previous, next;
    private Deque deque;

    public DequeItem() {
        previous = null;
        next = null;
        deque = null;
    }

    public Deque getDeque() {
        return deque;
    }

    protected void setDeque(Deque deque) {
        this.deque = deque;
    }

    public T getNext() {
        return next;
    }

    protected void setNext(T next) {
        this.next = next;
    }

    public T getPrevious() {
        return previous;
    }

    protected void setPrevious(T prev) {
        this.previous = prev;
    }
}
