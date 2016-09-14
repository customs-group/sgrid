package ml;

import libsvm.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * Created by edwardlol on 16/8/15.
 */
public class SVMTools {

    /**
     *
     * @param data
     * @param param
     * @return
     */
    public static svm_model train(Data data, svm_parameter param) {
        long startTime = System.currentTimeMillis();
        /* set svm problem */
        svm_problem problem = new svm_problem();
        problem.l = data.getSampleNum();
        problem.x = new svm_node[problem.l][];
        problem.y = new double[problem.l];
        for(int i = 0; i < problem.l; i++) {
            problem.x[i] = data.getDataSet("scaled").get(i);
            problem.y[i] = data.getLabels().get(i);
        }
        /* train svm model */
        String errorMsg = svm.svm_check_parameter(problem, param);
        if (errorMsg == null) {
            svm_model model = svm.svm_train(problem, param);
            try {
                svm.svm_save_model("./results/ml/model.txt", model);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Train finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
            return model;
        } else {
            System.out.println(errorMsg);
            return null;
        }
    }


    public static double regressionTest(svm_model model, Data data) {
        long startTime = System.currentTimeMillis();
        double diff = 0;
        int totalCnt = 0, goodCnt = 0;
        Vector<svm_node[]> set = data.getDataSet("original");
        Vector<Double> labels = data.getLabels();
        try {
            FileWriter fw = new FileWriter("./results/ml/result.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < data.getSampleNum(); i++) {
                svm_node[] sample = set.get(i);
                double real_label = labels.get(i);
                double predict_label = svm.svm_predict(model, sample);
                bw.write("predict label: " + predict_label + "; real label: " + real_label + "; ");
                for (int j = 0; j < data.getFeatureNum(); j ++) {
                    bw.write(sample[j].index + ":" + sample[j].value + " ");
                }
                bw.write("\n");
                bw.flush();
                totalCnt++;
                if (Math.abs(real_label - predict_label) / real_label < 0.3) {
                    goodCnt++;
                }
                diff += Math.pow(predict_label - real_label, 2);
            }
            diff /= data.getSampleNum();
            System.out.println("diff: " + diff);
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Test finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
        System.out.println(goodCnt);
        System.out.println(totalCnt);
        System.out.println("good per: " + 1.0 * goodCnt / totalCnt);
        return diff;
    }

    /**
     *
     * @param data
     * @param param
     * @param fold_n
     * @return
     */
    public static double crossValidation(Data data, svm_parameter param, int fold_n) {
        double totalDiff = 0.0d;
        for (int i = 0; i < fold_n; i++) {
            Vector<svm_node[]> trainSet = new Vector<>();
            Vector<svm_node[]> validSet = new Vector<>();
            Vector<Double> trainLabels = new Vector<>();
            Vector<Double> validLabels = new Vector<>();

            int vsLen = data.getSampleNum() / fold_n;
            int vsStart = i * vsLen;
            int vsEnd = (i + 1) * vsLen;

            for (int j = 0; j < vsStart; j++) {
                trainSet.add(data.getDataSet("scaled").get(j));
                trainLabels.add(data.getLabels().get(j));
            }
            for (int j = vsStart; j < vsEnd; j++) {
                validSet.add(data.getDataSet("scaled").get(j));
                validLabels.add(data.getLabels().get(j));
            }
            for (int j = vsEnd; j < data.getSampleNum(); j++) {
                trainSet.add(data.getDataSet("scaled").get(j));
                trainLabels.add(data.getLabels().get(j));
            }

            Data trainData = new Data(trainSet, trainLabels);
            svm_model model = train(trainData, param);
            if (model != null) {
                double diff = 0.0d;
                for (int j = 0; j < validSet.size(); j++) {
                    svm_node[] sample = validSet.get(i);
                    double real_label = validLabels.get(i);
                    double predict_label = svm.svm_predict(model, sample);
                    diff += Math.pow((predict_label - real_label), 2);
                }
                totalDiff += diff;
            }
        }
        return totalDiff / data.getSampleNum();
    }


    private static svm_print_interface svm_print_null = new svm_print_interface() {
        public void print(String s) {}
    };

    /**
     *
     * @param data
     * @return
     */
    public static svm_parameter update_param(Data data) {
        // no training outputs
        svm_print_interface print_func = svm_print_null;
        svm.svm_set_print_string_function(print_func);

        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.EPSILON_SVR;
        param.kernel_type = svm_parameter.RBF;
        param.eps = 0.001;
        param.p = 0.1;
        param.cache_size = 100;

        int best_power_of_c = -8;
        int best_power_of_g = -8;

        double smallestDiff = Double.MAX_VALUE;
        for (int power_of_c = -8; power_of_c < 8; power_of_c += 1) {
            for (int power_of_g = -8; power_of_g < 8; power_of_g += 1) {
                param.C = Math.pow(2, power_of_c);
                param.gamma = Math.pow(2, power_of_g);
                double diff = crossValidation(data, param, 10);

                System.out.println("power of c: " + power_of_c + "; power of g: " + power_of_g + "; diff: " + diff);
                if ((diff < smallestDiff)) {
                    smallestDiff = diff;
                    best_power_of_c = power_of_c;
                    best_power_of_g = power_of_g;
                }
            }
        }
        param.C = Math.pow(2, best_power_of_c);
        param.gamma = Math.pow(2, best_power_of_g);
        System.out.println("best C: " + param.C + "; best gamma: " + param.gamma + "; best diff: " + smallestDiff);
        return param;
    }



}
