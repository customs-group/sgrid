package ml;

import libsvm.*;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * Created by edwardlol on 16/7/7.
 */
public class Data {
    private int sampleNum = 0;
    private int featureNum = 0;
    private double scaleUpperBound = 1.0d;
    private double scaleLowerBound = -1.0d;

    private Vector<Double> labels = new Vector<>();
    private Vector<svm_node[]> originalSet = new Vector<>();
    private Vector<svm_node[]> scaledSet = null;

    public Data() {
    }
    public Data(Vector<svm_node[]> samples) {
        this.sampleNum = samples.size();
        this.originalSet = samples;
        this.scaledSet = this.originalSet;
        Iterator<svm_node[]> iterator = samples.iterator();
        if (iterator.hasNext()) {
            svm_node[] sample = iterator.next();
            this.featureNum = sample.length;
        }
    }
    public Data(Vector<svm_node[]> samples, Vector<Double> labels) {
        this.sampleNum = samples.size();
        this.originalSet = samples;
        this.scaledSet = this.originalSet;
        this.labels = labels;
        Iterator<svm_node[]> iterator = samples.iterator();
        if (iterator.hasNext()) {
            svm_node[] sample = iterator.next();
            this.featureNum = sample.length;
        }
    }

    /**
     *
     * @param file
     */
	public void readDataFromFile(String file) {
        long startTime = System.currentTimeMillis();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String[] contents = line.split(",");
                // y, x1, x2,...,xn
                this.featureNum = contents.length - 1;
                svm_node[] sample = new svm_node[this.featureNum];
                for (int i = 0; i < this.featureNum; i++) {
                    sample[i] = new svm_node();
                    sample[i].index = i + 1;
                    sample[i].value = Double.valueOf(contents[i + 1]);
                }
                this.originalSet.add(sample);
                this.labels.add(Double.valueOf(contents[0]));
                line = br.readLine();
            }
            this.sampleNum = this.originalSet.size();
            this.scaledSet = this.originalSet;
            // end data preparation
            System.out.println("Data preparation done in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
            System.out.println("Read " + this.getSampleNum() + " samples in total");
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Data preparation failed!");
            e.printStackTrace();
        }
	}

    /**
     * record data to file
     * @param fileName file name to store data
     * @param type type of data to be recorded, original or scaled
     */
    public void recordData(String fileName, String type) {
        long startTime = System.currentTimeMillis();
        String _fileName;
        Vector<svm_node[]> _set;
		/* set file name for record */
        switch (type.toLowerCase()) {
            case "original":
                _fileName = fileName + ".original.txt";
                _set = this.originalSet;
                break;
            case "scaled":
                _fileName = fileName + ".scaled.txt";
                _set = this.scaledSet;
                break;
            default:
                System.out.println("wrong data type, recording original set");
                _fileName = fileName + ".original.txt";
                _set = this.originalSet;
        }
        try {
            FileWriter fw = new FileWriter(_fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < this.sampleNum; i++) {
                bw.write(this.labels.get(i) + " ");
                svm_node[] sample = _set.get(i);
                for (int j = 0; j < this.featureNum; j++) {
                    bw.write(sample[j].index + ":" + sample[j].value + " ");
                }
                bw.write("\n");
                bw.flush();
            }
            System.out.println("Data record done in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
            System.out.println("see " + _fileName);
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Data record failed!");
            e.printStackTrace();
        }
    }

    /**
     * automatically scale the data according to the min/max value of each column
     * @return a scale_param is a double[][] that contains the min/max value of each column
     */
    public double[][] scaleData() {
        this.scaledSet = new Vector<>();
		/* step 0: initiate scale param */
        double[][] scale_param = new double[this.featureNum + 1][2];
        scale_param[0][0] = this.scaleUpperBound;
        scale_param[0][1] = this.scaleLowerBound;
		/* step 1: initiate feature bound */
        double[] feature_max = new double[this.featureNum];
        double[] feature_min = new double[this.featureNum];
        for(int i = 0; i < this.featureNum; i++) {
            feature_max[i] = -Double.MAX_VALUE;
            feature_min[i] = Double.MAX_VALUE;
        }
		/* step 2: find out min/max value */
        for (int i = 0; i < this.sampleNum; i++) {
            for (int j = 0; j < this.featureNum; j++) {
                feature_max[j] = Math.max(feature_max[j], this.originalSet.get(i)[j].value);
                feature_min[j] = Math.min(feature_min[j], this.originalSet.get(i)[j].value);
                scale_param[j + 1][0] = feature_max[j];
                scale_param[j + 1][1] = feature_min[j];
            }
        }
		/* step 3: scale */
        for (int i = 0; i < this.sampleNum; i++) {
            svm_node[] originalSample = this.originalSet.get(i);
            svm_node[] scaledSample = new svm_node[this.featureNum];
            for (int j = 0; j < this.featureNum; j++) {
                scaledSample[j] = new svm_node();
                scaledSample[j].index = originalSample[j].index;
                if (originalSample[j].value == feature_min[j]) {
                    scaledSample[j].value = this.scaleLowerBound;
                } else if (originalSample[j].value == feature_max[j]) {
                    scaledSample[j].value = this.scaleUpperBound;
                } else {
                    scaledSample[j].value = this.scaleLowerBound
                            + ((originalSample[j].value - feature_min[j])
                            / (feature_max[j] - feature_min[j])
                            * (this.scaleUpperBound - this.scaleLowerBound));
                }
            }
            this.scaledSet.add(scaledSample);
        }
        return scale_param;
    }

    /**
     * scale testing data
     * @param scaleParam
     * see: scaleData@param
     */
    public void scaleDataBy(double[][] scaleParam) {
        this.scaledSet = new Vector<>();
		/* step 1: initiate feature bound */
        this.scaleUpperBound = scaleParam[0][0];
        this.scaleLowerBound = scaleParam[0][1];
        /* step 2: read scale param */
        double[] feature_max = new double[this.featureNum];
        double[] feature_min = new double[this.featureNum];
        for(int i = 0; i < this.featureNum; i++) {
            feature_max[i] = scaleParam[i + 1][0];
            feature_min[i] = scaleParam[i + 1][1];
        }
		/* step 3: scale */
        for (int i = 0; i < this.sampleNum; i++) {
            svm_node[] sample = this.originalSet.get(i);
            svm_node[] scaled_sample = new svm_node[this.featureNum];
            for (int j = 0; j < this.featureNum; j++) {
                scaled_sample[j] = new svm_node();
                scaled_sample[j].index = sample[j].index;
                if (sample[j].value == feature_min[j]) {
                    scaled_sample[j].value = this.scaleLowerBound;
                } else if (sample[j].value == feature_max[j]) {
                    scaled_sample[j].value = this.scaleUpperBound;
                } else {
                    scaled_sample[j].value = this.scaleLowerBound
                            + ((sample[j].value - feature_min[j])
                            / (feature_max[j] - feature_min[j])
                            * (this.scaleUpperBound - this.scaleLowerBound));
                }



            }
            this.scaledSet.add(scaled_sample);
        }
    }

    /**
     * make the labels 1 or -1
     * @param label original label
     * @return normalized label
     */
    private static Double normalizeLabel(Double label) {
        return label <= 0 ? -1.0d : 1.0d;
    }


    /** get/set mothods */
    public Vector<svm_node[]> getDataSet(String type) {
        switch (type.toLowerCase()) {
            case "original":
                return this.originalSet;
            case "scaled":
                return this.scaledSet;
            default:
                System.out.println("wrong data type, original data set returned");
                return this.originalSet;
        }
    }
    public Vector<Double> getLabels() {
        return this.labels;
    }
    public int getSampleNum() {
        return this.sampleNum;
    }
    public int getFeatureNum() {
        return this.featureNum;
    }
    public void setScaleUpperBound(double scaleUpperBound) {
        this.scaleUpperBound = scaleUpperBound;
    }
    public void setScaleLowerBound(double scaleLowerBound) {
        this.scaleLowerBound = scaleLowerBound;
    }
}
