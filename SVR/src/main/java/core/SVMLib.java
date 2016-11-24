package core;

import com.google.common.base.Preconditions;
import libsvm.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Vector;

/**
 *
 * Created by edwardlol on 16/8/15.
 */
public class SVMLib {
    //~ Static fields and initializer ------------------------------------------

    private static SVMLib instance = null;

    public static boolean DEBUG = false;

    private static LibConfig config  = LibConfig.getInstance();

    public static svm_parameter svm_param;

    //~ Instance fields --------------------------------------------------------

    private Data trainingData;

    public double CStart;
    public double CStop;
    public double CStep;

    public double GStart;
    public double GStop;
    public double GStep;

    //~ Constructors -----------------------------------------------------------

    private SVMLib() {}

    //~ Methods ----------------------------------------------------------------

    /**
     * get the only instance of this class
     * @return the only instance of this class
     */
    public static SVMLib getInstance() {
        if (instance == null) {
            instance = new SVMLib();
        }
        return instance;
    }

    /**
     * init the lib from a file
     * @return this
     */
    public SVMLib initDataFromFile(String fileName) {
        this.trainingData = new Data().readDataFromCSVFile(fileName);
        return this;
    }

    /**
     * train the data sets
     * @return a trained model, can be used for validating
     */
    @Nullable
    public svm_model train() {
        svm_param.gamma = 1.0 / this.trainingData.getSampleNum();
        return train(this.trainingData);
    }

    /**
     * train the model with a given dataset
     * @param data training data sets
     * @return a trained model, can be used to validate test data
     */
    @Nullable
    static svm_model train(Data data) {
        Preconditions.checkNotNull(data);

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
        String errorMsg = svm.svm_check_parameter(problem, svm_param);
        if (errorMsg == null) {
            svm_model model = svm.svm_train(problem, svm_param);
            if (DEBUG) {
                try {
                    String modelFile = config.properties.getProperty("modelFile");
                    svm.svm_save_model(modelFile, model);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Train finished in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
                }
            }
            return model;
        } else {
            System.out.println(errorMsg);
            return null;
        }
    }

    /**
     * predict a sample's label according to the given model
     * @param sample new sample to be predicted
     * @param model svm model trained by training data
     * @return the predicted label of this sample
     */
    public static double predict(double[] sample, svm_model model) {
        svm_node[] svm_sample = new svm_node[sample.length];
        for (int i = 0; i < sample.length; i++) {
            svm_sample[i] = new svm_node();
            svm_sample[i].index = i;
            svm_sample[i].value = sample[i];
        }
        return svm.svm_predict(model, svm_sample);
    }

    /**
     * do cross validation on given dataset and given parameter
     * @param fold_n the number of folds
     * @return the 'loss' of the prediction
     */
    private double crossValidation(int fold_n) {
        double totalDiff = 0.0d;
        for (int i = 0; i < fold_n; i++) {
            Vector<svm_node[]> trainSet = new Vector<>();
            Vector<svm_node[]> validSet = new Vector<>();
            Vector<Double> trainLabels = new Vector<>();
            Vector<Double> validLabels = new Vector<>();

            int vsLen = this.trainingData.getSampleNum() / fold_n;
            int vsStart = i * vsLen;
            int vsEnd = (i + 1) * vsLen;

            for (int j = 0; j < vsStart; j++) {
                trainSet.add(this.trainingData.getDataSet("scaled").get(j));
                trainLabels.add(this.trainingData.getLabels().get(j));
            }
            for (int j = vsStart; j < vsEnd; j++) {
                validSet.add(this.trainingData.getDataSet("scaled").get(j));
                validLabels.add(this.trainingData.getLabels().get(j));
            }
            for (int j = vsEnd; j < this.trainingData.getSampleNum(); j++) {
                trainSet.add(this.trainingData.getDataSet("scaled").get(j));
                trainLabels.add(this.trainingData.getLabels().get(j));
            }

            Data trainData = new Data(trainSet, trainLabels);
            svm_model model = train(trainData);
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
        return totalDiff / this.trainingData.getSampleNum();
    }

    /**
     * use grid search to optimize svm_parameter
     * @return the optimized svm_parameter
     */
    @SuppressWarnings("unused")
    public svm_parameter gridSearch(boolean approximate, int fold_n) {
        // suppress training outputs
        svm_print_interface print_func = config.svm_print_null;
        svm.svm_set_print_string_function(print_func);

        double bestC = 1.0d;
        double bestG = 1.0d / this.trainingData.getSampleNum();
        double smallestDiff = Double.MAX_VALUE;

        for (double c = this.CStart; c < this.CStop; c += this.CStep) {
            svm_param.C = approximate ? Math.pow(2, c) : c;

            // check if default g gives better result
            svm_param.gamma = 1.0 / this.trainingData.getSampleNum();
            double diff = crossValidation(fold_n);
            if ((diff < smallestDiff)) {
                smallestDiff = diff;
                bestC = svm_param.C;
                bestG = svm_param.gamma;
                System.out.println("best c: " + bestC + "; best g: " + bestG + "; diff: " + diff);
            }

            for (double g = this.GStart; g < this.GStop; g += this.GStep) {
                svm_param.gamma = approximate ? Math.pow(2, g) : g;
                diff = crossValidation(fold_n);
                if ((diff < smallestDiff)) {
                    smallestDiff = diff;
                    bestC = svm_param.C;
                    bestG = svm_param.gamma;
                    System.out.println("best c: " + bestC + "; best g: " + bestG + "; diff: " + diff);
                }
            }
        }
        svm_param.C = bestC;
        svm_param.gamma = bestG;
        System.out.println("best C: " + svm_param.C + "; best gamma: " + svm_param.gamma + "; best diff: " + smallestDiff);
        return svm_param;
    }

    /**
     * set the type of this lib, see{@link LibConfig.Type}
     * @param type type
     * @return this
     */
    public SVMLib setType(LibConfig.Type type) {
        config.type = type;
        svm_param = config.getDefaultParam();
        return this;
    }

}

// End SVMLib.java
