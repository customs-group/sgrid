package edwardlol;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by edwardlol on 16/7/9.
 */
public abstract class AbstractListLib<E> {
    protected final List<E> elements = new ArrayList<>();

    public void add(E e) {
        this.elements.add(e);
    }

    public E get(int i) {
        return this.elements.get(i);
    }

    public void remove(E cluster) {
        this.elements.remove(cluster);
    }

    public void remove(int i) {
        this.elements.remove(i);
    }

    public int size() {
        return this.elements.size();
    }

    public List<E> getElements() {
        return this.elements;
    }

}
