package core.cluster;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by edwardlol on 16/7/8.
 */
public abstract class AbstractCluster<E> {

    //~ Instance fields --------------------------------------------------------

    protected final Set<E> samples = new HashSet<>();

    //~ Methods ----------------------------------------------------------------

    /**
     * add a new sample to the cluster
     * @param e new sample
     */
    public void add(E e) {
        this.samples.add(e);
    }

    /**
     * get the sample number of this cluster
     * @return the sample number of this cluster
     */
    public int sampleNum() {
        return this.samples.size();
    }

    /**
     * union this cluster with another one
     * @param another another cluster to be unioned
     * @return this
     */
    public AbstractCluster<E> union(AbstractCluster<E> another) {
        this.samples.addAll(another.samples);
        return this;
    }

    /**
     * get all samples in the cluster
     * @return a set of all samples in the cluster
     */
    public Set<E> getSamples() {
        return this.samples;
    }

    /**
     * get the label of this cluster
     * @param labelType the type of the label,
     *                  should be specific in the class
     *                  that extends this class
     * @return the label of this cluster in double
     */
    public abstract double getLabel(String labelType);
}

// End AbstractCluster.java
