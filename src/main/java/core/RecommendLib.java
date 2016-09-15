package core;

import util.*;
import core.columnGroups.*;

import java.io.*;
import java.util.*;

/**
 *
 * Created by edwardlol on 16/7/9.
 */
public class RecommendLib {

    //~ Static fields/initializers ---------------------------------------------

    private static RecommendLib instance = null;

    //~ Instance fields --------------------------------------------------------

    private final List<DefectSimple> defects = new ArrayList<>(); // a list of all defects
    private final Map<String, Set<Integer>> invertedIndex = new HashMap<>(); // inverted index of the corpus
    private final List<String> featureTerms = new ArrayList<>(); // the terms to make feature vectors

    private double threshold = 0.0d;
    private int keyWordBoundary = 255; // the maximun number of key words of each defect
    private int featureVectorBoundary = 1280; // the maximun length of feature vector

    //~ Constructors -----------------------------------------------------------

    private RecommendLib() {}

    //~ Methods ----------------------------------------------------------------

    /**
     * get the only instance of this class
     * @return the only instance of this class
     */
    public static RecommendLib getInstance() {
        if (instance == null) {
            instance = new RecommendLib();
        }
        return instance;
    }

    /**
     * init the lib from a given csv file
     * @param file name of the given file
     */
    public void initFromCSVFile(String file) {
        long startTime = System.currentTimeMillis();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine(); // first line is column names
            line = bufferedReader.readLine();
            while (line != null) {
                String[] contents = line.split(",");
                String defectApperance = Util.readStringWithNull(contents[15]);
                String defectDescription = Util.readStringWithNull(contents[16]);
                String recommendation = Util.readStringWithNull(contents[20]);
                Document _defectDescription = new Document(defectDescription);

                Details details = new Details.DetailsBuilder(defectApperance, _defectDescription)
                        .recommendation(recommendation).createDetails();

                DefectSimple defect = new DefectSimple.Builder().details(details).build();
                this.defects.add(defect);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("data reading finished in " + (finishTime - startTime) / 1000.0 + " seconds");
        this.train();
    }

    /**
     * train the lib
     */
    private void train() {
        long startTime = System.currentTimeMillis();
        // seperate the description and count the terms of each defect
        this.defects.forEach(defect -> {
            List<String> seperatedDescription = Util.seperate(defect.getDescription().toString());
            defect.getDescription().countTerms(seperatedDescription);
        });
        // calculate the inverted index of the corpus
        for (int i = 0; i < this.defects.size(); i++) {
            for (String term : this.defects.get(i).getDescription().getTermsCount().keySet()) {
                Set<Integer> indexes = this.invertedIndex.containsKey(term) ? this.invertedIndex.get(term) : new HashSet<>();
                indexes.add(i);
                this.invertedIndex.put(term, indexes);
            }
        }
        // calculate the TF-IDF of each term in each defect
        this.defects.forEach(defect ->
                defect.getDescription().calculateTF_IDF(this.defects.size(), this.invertedIndex));
        // calculate the key words of each defect's description
        Set<String> globalKeyWords = new HashSet<>();
        this.defects.forEach(defect ->
                globalKeyWords.addAll(defect.getDescription().calculateKeyWords(this.keyWordBoundary)));
        this.calculateFeatureTerms(globalKeyWords);
        // calculate the feature vector of each defect in the defect list
        this.defects.forEach(defect ->
                defect.getDescription().calculateFeatureVector(this.featureTerms));

        long finishTime = System.currentTimeMillis();
        System.out.println("training finished in " + (finishTime - startTime) / 1000.0 + " seconds");
    }

    /**
     * recommend a list of solutions according to the input description
     * @param description the input description of recommend
     * @param displayBoundary the boundary to display recommend solutions
     * @return a list of similar defects and their solutions
     */
    public List<DefectSimple> recommend(String description, int displayBoundary) {
        assert this.defects.size() != 0;

        long startTime = System.currentTimeMillis();

        Document _defectDescription = new Document(description);
        _defectDescription.countTerms(Util.seperate(_defectDescription.toString()));
        _defectDescription.calculateTF_IDF(this.defects.size(), this.invertedIndex);
        _defectDescription.calculateKeyWords(this.keyWordBoundary);
        _defectDescription.calculateFeatureVector(this.featureTerms);

        double[] newFeatureVector = _defectDescription.getFeatureVector();
        Map<DefectSimple, Double> defectMap = new HashMap<>();
        for (DefectSimple oldDefect : this.defects) {
            double[] oldFeatureVector = oldDefect.getDescription().getFeatureVector();
            double similarity = 0.0;
            for (int i = 0; i < newFeatureVector.length; i++) {
                similarity += newFeatureVector[i] * oldFeatureVector[i];
            }
            if (similarity > this.threshold) {
                defectMap.put(oldDefect, similarity);
            }
        }
        List<Map.Entry<DefectSimple, Double>> resultMapList = new ArrayList<>(defectMap.entrySet());
        Collections.sort(resultMapList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        List<DefectSimple> resultList = new ArrayList<>();
        for (int i = 0; i < displayBoundary; i++) {
            DefectSimple defect = resultMapList.get(i).getKey();
            resultList.add(defect);
            System.out.println("recommend solution " + (i + 1) + ", similarity: " + resultMapList.get(i).getValue());
            System.out.println(defect.getDetails().toString());
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("searching done in " + (finishTime - startTime) / 1000.0 + " seconds");
        return resultList;
    }

    /**
     * calculate the feature terms of the corpus
     * @param globalKeyWords a set of all key words of every defect
     */
    private void calculateFeatureTerms(Set<String> globalKeyWords) {
        // setup a list of global keywords and their count
        Map<String, Integer> globalKeyWordsCount = new HashMap<>();
        globalKeyWords.forEach(keyWord -> globalKeyWordsCount.put(keyWord, this.invertedIndex.get(keyWord).size()));
        List<Map.Entry<String, Integer>> globalKeyWordsCountList = new ArrayList<>(globalKeyWordsCount.entrySet());
        Collections.sort(globalKeyWordsCountList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        // set the boundary of feature terms
        int realBoundary = Math.min(this.featureVectorBoundary, globalKeyWordsCountList.size());
        globalKeyWordsCountList = globalKeyWordsCountList.subList(0, realBoundary);
        // set the feature terms
        for (Map.Entry<String, Integer> entry : globalKeyWordsCountList) {
            String term = entry.getKey();
            this.featureTerms.add(term);
        }
    }

    /**
     * sort the terms in the inverted index according to the number of
     * articles containing the term
     * @return a list of sorted inverted index in descending order
     */
    private List<Map.Entry<String, Set<Integer>>> sortInvertedIndex() {
        List<Map.Entry<String, Set<Integer>>> invertedIndexList = new ArrayList<>(this.invertedIndex.entrySet());
        Collections.sort(invertedIndexList, (entry1, entry2) -> {
            if (entry2.getValue().size() == entry1.getValue().size()) {
                return 0;
            } else if (entry2.getValue().size() > entry1.getValue().size()) {
                return 1;
            } else {
                return -1;
            }
        });
        return invertedIndexList;
    }

    //~ Setter methods ---------------------------------------------------------

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
    public void setKeyWordBoundary(int keyWordBoundary) {
        this.keyWordBoundary = keyWordBoundary;
    }
    public void setFeatureVectorBoundary(int featureVectorBoundary) {
        this.featureVectorBoundary = featureVectorBoundary;
    }
}

// End RecommendLib.java
