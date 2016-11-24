package core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import util.CollectionUtil;

import java.io.Serializable;
import java.util.*;


/**
 *
 * Created by edwardlol on 16/6/14.
 */
public class Document implements Serializable {

    //~ Static fields/initializers ---------------------------------------------

    // not final because there's possibility that you may want to edit the
    // content of the document
    private String content;

    private List<String> terms;
    private Map<String, Integer> termsCount;
    private Map<String, Double> termsTF_IDF;
    private Set<String> keyWords;
    private double[] featureVector;

    //~ Constructors -----------------------------------------------------------

    public Document(String content) {
        this.content = content;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * calculate the appear times of each term
     * @param terms a list of terms to count
     * @return a map of terms and their counts
     */
    public Map<String, Integer> countTerms(List<String> terms) {
        this.terms = Lists.newArrayList(terms);
        this.termsCount = Maps.newHashMap();
        this.termsCount = CollectionUtil.countList(terms);
        return this.termsCount;
    }

    /**
     * calculate the TF-IDF for each term
     * @param docCount total document count in the corpus
     * @param invertedIndex the inverted index of the corpus
     * @return a map of terms and their TF-IDF
     */
    public Map<String, Double> calculateTF_IDF(int docCount, Map<String, Set<Integer>> invertedIndex) {
        this.termsTF_IDF = Maps.newHashMap();
        int wordsNum = 0;
        for (int i : this.termsCount.values()) { wordsNum += i; }

        for (Map.Entry<String, Integer> entry : this.termsCount.entrySet()) {
            String term = entry.getKey();
            int count = entry.getValue();
            // calculate tf
            double tf = 1.0d * count / wordsNum;
            // calculate idf
            int includeCount = invertedIndex.containsKey(term) ? invertedIndex.get(term).size() : 0;
            double idf = Math.log(1.0d * docCount / (includeCount + 1));

            this.termsTF_IDF.put(term, tf * idf);
        }
        return this.termsTF_IDF;
    }

    /**
     * set the TF-IDF of all words and
     * get the keywords of the description
     * according to TF-IDF
     * @param boundary number of keywords
     *                 if the description contains less than @boundary@
     *                 words it will return all words in the description
     * @return a set of key words
     */
    public Set<String> calculateKeyWords(int boundary) {
        this.keyWords = Sets.newHashSet();
        // sort this.termsTF_IDF
        List<Map.Entry<String, Double>> termsEntryList = Lists.newArrayList(this.termsTF_IDF.entrySet());
        Collections.sort(termsEntryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        // get top x terms and their TF-IDF
        int realBoundary = Math.min(boundary, termsEntryList.size());

        for (int i = 0; i < realBoundary; i++) {
            String keyWord = termsEntryList.get(i).getKey();
            this.keyWords.add(keyWord);
        }

        return this.keyWords;
    }

    /**
     * calculate the feature vector of the description
     * according to the feature terms of the corpus
     * @param featureTerms the feature terms of the corpus
     */
    public void calculateFeatureVector(List<String> featureTerms) {
        this.featureVector = new double[featureTerms.size()];
        for (int i = 0; i < this.featureVector.length; i++) {
            String keyWord = featureTerms.get(i);
            if (this.keyWords.contains(keyWord)) {
                this.featureVector[i] = this.termsTF_IDF.get(keyWord);
            }
        }
        // normalization
        double sum = 0.0;
        for (double temp : this.featureVector) {
            sum += Math.pow(temp, 2);
        }
        sum = Math.sqrt(sum);
        for (int i = 0; i < this.featureVector.length; i++) {
            this.featureVector[i] /= sum;
        }
    }

    @Override
    public String toString() {
        return this.content;
    }

    /**
     * check the result of word seperation
     * for debug usage, like making a dictionary of word seperation
     * @return a String representing the result of word seperation
     */
    public String checkSep() {
        String result = "";
        for (int i = 0; i < this.terms.size(); i++) {
            result += terms.get(i) + " ";
            if (i < this.terms.size()) {
                result += " ";
            }
        }
        return result;
    }

    //~ getter methods ---------------------------------------------------------

    public List<String> getTerms() {
        return this.terms;
    }
    public Map<String, Integer> getTermsCount() {
        return this.termsCount;
    }
    public Map<String, Double> getTermsTF_IDF() {
        return this.termsTF_IDF;
    }
    public Set<String> getKeyWords() {
        return this.keyWords;
    }
    public double[] getFeatureVector() {
        return this.featureVector;
    }

}

// End Document.java
