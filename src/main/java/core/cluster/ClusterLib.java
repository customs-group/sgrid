package core.cluster;

import core.DefectComplete;
import core.Document;
import core.columnGroups.*;
import edwardlol.*;

import java.io.*;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by edwardlol on 16/7/9.
 */
public class ClusterLib extends AbstractListLib<Cluster> implements Serializable {

    private String labelName;

    public void initFromFile(String file) {
        long startTime = System.currentTimeMillis();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine(); // the first line is column name
            line = bufferedReader.readLine();
            while (line != null) {
                String[] contents = line.split(",");
                BigInteger id = new BigInteger(Utility.readStringWithNull(contents[2]));
                String company = Utility.readStringWithNull(contents[3]);
                String department = Utility.readStringWithNull(contents[4]);
                String location = Utility.readStringWithNull(contents[5]);
                String defectLevel = Utility.readStringWithNull(contents[6]);
                String type = Utility.readStringWithNull(contents[7]);
                String classification = Utility.readStringWithNull(contents[8]);
                String equipName = Utility.readStringWithNull(contents[9]);
                String equipType = Utility.readStringWithNull(contents[10]);
                String functionPosition = Utility.readStringWithNull(contents[11]);
                String partsName = Utility.readStringWithNull(contents[12]);
                String voltageString = Utility.readStringWithNull(contents[13]);
                Calendar findDate = Utility.stringToCalendar(contents[14]);
                String defectApperance = Utility.readStringWithNull(contents[15]);
                String defectDescription = Utility.readStringWithNull(contents[16]);
                String defectType = Utility.readStringWithNull(contents[17]);
                // TODO: 16/6/12 defectClass: the added column
                String defectClass = Utility.readStringWithNull(contents[18]);
                Calendar reportDate = Utility.stringToCalendar(contents[19]);
                Calendar solveDate = Utility.stringToCalendar(contents[20]);
                String recommendation = Utility.readStringWithNull(contents[21]);
                String manufactor = Utility.readStringWithNull(contents[22]);
                String model = Utility.readStringWithNull(contents[23]);
                String defectReason = Utility.readStringWithNull(contents[24]);
                String defectPart = Utility.readStringWithNull(contents[25]);
                // 26 解决方案
                String defectStatus = Utility.readStringWithNull(contents[27]);
                // 28 设备生产日期
                Calendar operationDate = Utility.stringToCalendar(contents[29]);

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
                this.add(cluster);

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
        float[][] distanceMatrix = new float[this.size()][this.size()];
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = i + 1; j < distanceMatrix.length; j++) {
                distanceMatrix[i][j] = (float) Math.abs(this.get(i).getLabel(this.labelName) - this.get(j).getLabel(this.labelName));
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
            int cluster_i_num = this.get(index_i).size();
            int cluster_j_num = this.get(index_j).size();
            distanceMatrix = MyMath.updateDistanceMatrix(distanceMatrix, index_i, index_j, cluster_i_num, cluster_j_num);
            // update clusterList
            this.get(index_i).union(this.get(index_j));
            this.remove(index_j);
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

            Collections.sort(this.elements, (cluster1, cluster2) ->
                    cluster1.size() == cluster2.size() ? 0
                            : cluster1.size() > cluster2.size() ? -1 : 1
            );

            this.elements.forEach(cluster -> {
                if (cluster.getElements().size() > 1) {
                    try {
                        bufferedWriter.write("cluster label: " + cluster.getLabel("hours") + "; count: " + cluster.getElements().size() + "\n");
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
