package core;

import libsvm.*;

import java.util.Vector;

/**
 * Created by edwardlol on 16-11-22.
 */
@SuppressWarnings("unused")
// not using this class for now
public class SVM_Parameter {

    public double CStart;
    public double CStop;
    public double CStep;

    public double GStart;
    public double GStop;
    public double GStep;

    private svm_parameter svm_param;

    private LibConfig config  = LibConfig.getInstance();


    double crossValidation(Data data, int fold_n) {
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
            svm_model model = SVMLib.train(trainData);
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

    public svm_parameter gridSearchByStep(Data data) {
        // suppress training outputs
        svm_print_interface print_func = this.config.svm_print_null;
        svm.svm_set_print_string_function(print_func);

        double bestC = 1.0d;
        double bestG = 1.0 / data.getSampleNum();
        double smallestDiff = Double.MAX_VALUE;

        for (double c = CStart; c < CStop; c += CStep) {
            this.svm_param.C = c;

            for (double g = GStart; g < GStop; g += GStep) {
                this.svm_param.gamma = g;
                double diff = crossValidation(data, 10);

                if ((diff < smallestDiff)) {
                    smallestDiff = diff;
                    bestC = this.svm_param.C;
                    bestG = this.svm_param.gamma;
                    System.out.println("best c: " + bestC + "; best g: " + bestG + "; diff: " + diff);
                }
            }
        }
        this.svm_param.C = bestC;
        this.svm_param.gamma = bestG;
        System.out.println("best C: " + this.svm_param.C + "; best gamma: " + this.svm_param.gamma + "; best diff: " + smallestDiff);
        return this.svm_param;
    }

    public svm_parameter gridSearchBySquare(Data data) {
        // suppress training outputs
        svm_print_interface print_func = this.config.svm_print_null;
        svm.svm_set_print_string_function(print_func);

        double bestC = 1.0d;
        double bestG = 1.0 / data.getSampleNum();
        double smallestDiff = Double.MAX_VALUE;

        for (double c = CStart; c < CStop; c *= CStep) {
            this.svm_param.C = c;

            for (double g = GStart; g < GStop; g *= GStep) {
                this.svm_param.gamma = g;
                double diff = crossValidation(data, 10);

                if ((diff < smallestDiff)) {
                    smallestDiff = diff;
                    bestC = this.svm_param.C;
                    bestG = this.svm_param.gamma;
                    System.out.println("best c: " + bestC + "; best g: " + bestG + "; diff: " + diff);
                }
            }
        }
        this.svm_param.C = bestC;
        this.svm_param.gamma = bestG;
        System.out.println("best C: " + this.svm_param.C + "; best gamma: " + this.svm_param.gamma + "; best diff: " + smallestDiff);
        return this.svm_param;
    }

    public SVM_Parameter setC(double C) {
        this.svm_param.C = C;
        return this;
    }
}
