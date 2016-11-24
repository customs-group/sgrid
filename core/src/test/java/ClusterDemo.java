import core.cluster.ClusterLib;

/**
 *
 * Created by edwardlol on 16/8/13.
 */
public class ClusterDemo {
    public static void main(String[] args) {
        ClusterLib clusterLib = ClusterLib.getInstance();
        clusterLib.initFromCSVFile("datasets/defects/SwitchDefects.cleared.csv");
        clusterLib.setLabelName("hours");
        clusterLib.hierarchicalCluster(1.0);
        clusterLib.recordClusterResult("results/defects/cluster/hours/all.txt");
    }
}
