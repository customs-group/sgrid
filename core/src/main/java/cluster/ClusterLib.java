package core.cluster;

import core.DefectComplete;
import core.Document;
import core.columnGroups.*;
import util.*;
import util.DTMath;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by edwardlol on 16/7/9.
 */
public class ClusterLib implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    private static ClusterLib instance = null;

    //~ Instance fields --------------------------------------------------------

    private List<Cluster> clusters = new ArrayList<>();

    private String labelName;

    //~ Constructors -----------------------------------------------------------

    private ClusterLib() {}

    //~ Methods ----------------------------------------------------------------

    /**
     * get the only instance of this class
     * @return the only instance of this class
     */
    public static ClusterLib getInstance() {
        if (instance == null) {
            instance = new ClusterLib();
        }
        return instance;
    }

    public void initFromCSVFile(String file) {
        long startTime = System.currentTimeMillis();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine(); // the first line is column name
            line = bufferedReader.readLine();
            while (line != null) {
                String[] contents = line.split(",");
                BigInteger id = new BigInteger(Util.readStringWithNull(contents[2]));
                String company = Util.readStringWithNull(contents[3]);
                String department = Util.readStringWithNull(contents[4]);
                String location = Util.readStringWithNull(contents[5]);
                String defectLevel = Util.readStringWithNull(contents[6]);
                String type = Util.readStringWithNull(contents[7]);
                String classification = Util.readStringWithNull(contents[8]);
                String equipName = Util.readStringWithNull(contents[9]);
                String equipType = Util.readStringWithNull(contents[10]);
                String functionPosition = Util.readStringWithNull(contents[11]);
                String partsName = Util.readStringWithNull(contents[12]);
                String voltageString = Util.readStringWithNull(contents[13]);
                Calendar findDate = Util.stringToCalendar(contents[14]);
                String defectApperance = Util.readStringWithNull(contents[15]);
                String defectDescription = Util.readStringWithNull(contents[16]);
                String defectType = Util.readStringWithNull(contents[17]);
                // TODO: 16/6/12 defectClass: the added column
                String defectClass = Util.readStringWithNull(contents[18]);
                Calendar reportDate = Util.stringToCalendar(contents[19]);
                Calendar solveDate = Util.stringToCalendar(contents[20]);
                String recommendation = Util.readStringWithNull(contents[21]);
                String manufactor = Util.readStringWithNull(contents[22]);
                String model = Util.readStringWithNull(contents[23]);
                String defectReason = Util.readStringWithNull(contents[24]);
                String defectPart = Util.readStringWithNull(contents[25]);
                // 26 解决方案
                String defectStatus = Util.readStringWithNull(contents[27]);
                // 28 设备生产日期
                Calendar operationDate = Util.stringToCalendar(contents[29]);

                int voltage;
                Pattern vPattern = Pattern.compile("(\\d+)[vV]");
                Pattern kvPattern = Pattern.compile("(\\d+)[kK][vV]");
                Matcher vMatcher = vPattern.matcher(voltageString);
                Matcher kvMatcher = kvPattern.matcher(voltageString);
                if (vMatcher.find()) {
                    voltage = Integer.parseInt(vMatcher.group(1)) / 1000;
                } else if (kvMatcher.find()) {
                    voltage = Integer.parseInt(kvMatcher.group(1));
                } else {
                    voltage = 0;
                }

                Document _defectDescription = new Document(defectDescription);
                Overview overview = new Overview.OverviewBuilder(defectLevel, defectType,
                        defectClass, defectStatus).createOverview();
                Details details = new Details.DetailsBuilder(defectApperance, _defectDescription)
                        .defectReason(defectReason).recommendation(recommendation).createDetails();
                DateInfo dateInfo = new DateInfo.DateInfoBuilder(reportDate, operationDate)
                        .findDate(findDate).solvedDate(solveDate).createDateInfo();
                BelongingInfo belongingInfo = new BelongingInfo.BelongingInfoBuilder(company,
                        department, location, manufactor).createBelongingInfo();
                EquipInfo equipInfo = new EquipInfo.EquipInfoBuilder(equipName, equipType, voltage)
                        .functionPosition(functionPosition).partsName(partsName).model(model).createEquipInfo();

                DefectComplete defect = new DefectComplete.Builder().overview(overview).details(details)
                        .dateInfo(dateInfo).equipInfo(equipInfo).belongingInfo(belongingInfo).build();
//                defect.id = id;

                Cluster cluster = new Cluster();
                cluster.add(defect);
                this.clusters.add(cluster);

                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("data reading finished in " + (finishTime - startTime) / 1000.0 + " seconds");
    }

    /**
     * main loop of hierarchical clustering
     * @param threshold stopping threshold of clustering
     */
    public void hierarchicalCluster(double threshold) {
        // initFromCSVFile matrix
        float[][] distanceMatrix = new float[this.clusters.size()][this.clusters.size()];
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = i + 1; j < distanceMatrix.length; j++) {
                distanceMatrix[i][j] = (float) java.lang.Math.abs(this.clusters.get(i).getLabel(this.labelName) - this.clusters.get(j).getLabel(this.labelName));
                distanceMatrix[j][i] = distanceMatrix[i][j];
            }
        }
        // main loop
        while (true) {
            float minInterval = Float.MAX_VALUE;
            int index_i = 0, index_j = 0;
            for (int i = 0; i < distanceMatrix.length; i++) {
                for (int j = i + 1; j < distanceMatrix.length; j++) {
                    if (distanceMatrix[i][j] < minInterval) {
                        minInterval = distanceMatrix[i][j];
                        index_i = i;
                        index_j = j;
                    }
                }
            }
            if (minInterval > threshold) {
                break;
            }
            // update matrix
            int cluster_i_num = this.clusters.get(index_i).sampleNum();
            int cluster_j_num = this.clusters.get(index_j).sampleNum();
            distanceMatrix = DTMath.updateDistanceMatrix(distanceMatrix, index_i, index_j, cluster_i_num, cluster_j_num);
            // update clusterList
            this.clusters.get(index_i).union(this.clusters.get(index_j));
            this.clusters.remove(index_j);
        }
    }

    /**
     *
     * @param file
     */
    public void recordClusterResult(String file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            Collections.sort(this.clusters, (cluster1, cluster2) ->
                    cluster1.sampleNum() == cluster2.sampleNum() ? 0
                            : cluster1.sampleNum() > cluster2.sampleNum() ? -1 : 1
            );

            this.clusters.forEach(cluster -> {
                if (cluster.getSamples().size() > 1) {
                    try {
                        bufferedWriter.write("cluster label: " + cluster.getLabel("hours") + "; count: " + cluster.getSamples().size() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        bufferedWriter.write("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
