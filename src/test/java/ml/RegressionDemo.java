package ml;

import libsvm.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 * Created by edwardlol on 16/8/13.
 */
public class RegressionDemo {

    private static void oldRegression() {
        Data totalData = new Data();
        totalData.readDataFromFile("./datasets/ml/total.csv");
        totalData.recordData("./results/ml/total", "original");
        svm_parameter param = SVMTools.update_param(totalData);

        Data trainData = new Data();
        Data testData = new Data();
        /* read data */
        trainData.readDataFromFile("./datasets/ml/train.csv");
        testData.readDataFromFile("./datasets/ml/test.csv");
        /* record data */
        trainData.recordData("./results/ml/train", "original");
        testData.recordData("./results/ml/test", "original");

        svm_model model = SVMTools.train(trainData, param);
        regression_test(model, trainData);
        regression_test(model, testData);
    }

    private static void regression_test(svm_model model, Data Data) {
        long startTime = System.currentTimeMillis();
        double diff = 0;
        Vector<svm_node[]> set = Data.getDataSet("original");
        Vector<Double> labels = Data.getLabels();
        try {
            FileWriter fw = new FileWriter("./results/ml/result.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            for (int i = 0; i < Data.getSampleNum(); i++) {
                svm_node[] sample = set.get(i);
                double real_label = labels.get(i);
                double predict_label = svm.svm_predict(model, sample);
                bw.write("predict label: " + predict_label + "; real label: " + real_label + "; ");
                for (int j = 0; j < Data.getFeatureNum(); j ++) {
                    bw.write(sample[j].index + ":" + sample[j].value + " ");
                }
                bw.write("\n");
                bw.flush();
                diff += Math.pow(predict_label - real_label, 2);
            }
            diff /= Data.getSampleNum();
            System.out.println("diff: " + diff);
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Test finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
    }



    private static Map<LocalDate, String> readPart1(String file) {
        Map<LocalDate, String> defcnt = new HashMap<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA);

            String line = br.readLine(); // first line is shit
            line = br.readLine();
            while (line != null) {
                String[] contents = line.split(",");
                LocalDate date = LocalDate.parse(contents[0], formatter);
                String cnt = contents[1];

                defcnt.put(date, cnt);
                line = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defcnt;
    }

    private static Map<LocalDate, Integer[]> readPart2(String file) {
        Map<LocalDate, Integer[]> weather = new HashMap<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA);

            String line = br.readLine();
            while (line != null) {
                String[] contents = line.split(",");
                int monthValue = Integer.parseInt(contents[1]);
                String month = String.format("%02d", monthValue);
                int dayValue = Integer.parseInt(contents[2]);
                String day = String.format("%02d", dayValue);

                LocalDate date = LocalDate.parse(contents[0] + "-" + month + "-" + day, formatter);
                Integer[] wthFeature = new Integer[4];
                for (int i = 0; i < wthFeature.length; i++) {
                    wthFeature[i] = Integer.parseInt(contents[i + 3]);
                }
                weather.put(date, wthFeature);
                line = br.readLine();
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weather;
    }

    private static void newRegression() {
        Map<LocalDate, String> defcnt = readPart1("./datasets/svr/defcnt.csv");
        Map<LocalDate, Integer[]> weather = readPart2("./datasets/svr/weather.csv");

        Vector<svm_node[]> samples = new Vector<>();
        Vector<Double> labels = new Vector<>();

        for (Map.Entry<LocalDate, String> entry : defcnt.entrySet()) {
            LocalDate key = entry.getKey();
            if (weather.containsKey(key)) {
                svm_node[] sample = new svm_node[weather.get(key).length];
                for (int i = 0; i < weather.get(key).length; i++) {
                    sample[i] = new svm_node();
                    sample[i].index = i + 1;
                    sample[i].value = weather.get(key)[i];
                }
                samples.add(sample);
                labels.add(Double.parseDouble(entry.getValue()));
            }
        }

        Data data = new Data(samples, labels);

//        SVMTools.update_param(data);

        int index = (int)Math.round(samples.size() * 0.9);

        Vector<svm_node[]> trainSamples = new Vector<>(samples.subList(0, index));
        Vector<Double> trainLabels = new Vector<>(labels.subList(0, index));

        Vector<svm_node[]> testSamples = new Vector<>(samples.subList(index, samples.size()));
        Vector<Double> testLabels = new Vector<>(labels.subList(index, samples.size()));

        Data trainData = new Data(trainSamples, trainLabels);
        Data testData = new Data(testSamples, testLabels);
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.EPSILON_SVR;
        param.kernel_type = svm_parameter.RBF;
        param.eps = 0.001;
        param.p = 0.1;
        param.cache_size = 100;
        param.C = 8;
        param.gamma = 0.03125;
        svm_model model = SVMTools.train(trainData, param);
        SVMTools.regressionTest(model, testData);
    }

    public static void main(String[] args) {
        newRegression();
    }
}
