package edwardlol;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by edwardlol on 16/7/8.
 */
public abstract class AbstractCluster<E> {
    protected final Set<E> elements = new HashSet<>();

    /**
     * add a new element to the cluster
     * @param e new element
     */
    public void add(E e) {
        this.elements.add(e);
    }

    /**
     * return the size of the cluster
     * @return the size of the cluster
     */
    public int size() {
        return this.elements.size();
    }

    /**
     * union this cluster with another one
     * @param another another cluster to be unioned
     * @return this
     */
    public AbstractCluster<E> union(AbstractCluster<E> another) {
        this.elements.addAll(another.elements);
        return this;
    }

    /**
     * get all elements in the cluster
     * @return a set of all elements in the cluster
     */
    public Set<E> getElements() {
        return this.elements;
    }

    /**
     * get the label of this cluster
     * @param labelType the type of the label,
     *                  can be specific in the class
     *                  that extends this class
     * @return the label of this cluster in double
     */
    public abstract double getLabel(String labelType);
}
