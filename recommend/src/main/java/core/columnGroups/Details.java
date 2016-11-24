package core.columnGroups;

import core.Document;

import java.io.Serializable;

/**
 *
 * Created by edwardlol on 16/5/29.
 */
public class Details implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private final String apperance; // 缺陷表象
    private final Document description; // 缺陷描述
    private final String recommendation; // 处理意见
    private final String defectPart; // 缺陷部位
    private final String defectReason; // 缺陷原因
    private final String solution; // 解决方案
    private final String comments; // 备注

    //~ Constructors -----------------------------------------------------------

    /** private constructer
     * use Builder to get an instance of this class
     */
    private Details(String apperance, Document description, String recommendation,
                    String defectPart, String defectReason, String solution, String comments) {
        this.apperance = apperance;
        this.description = description;
        this.recommendation = recommendation;
        this.defectPart = defectPart;
        this.defectReason = defectReason;
        this.solution = solution;
        this.comments = comments;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String toString() {
        return "缺陷表象: " + this.apperance
                + "\n缺陷描述: " + this.description.toString()
                + "\n处理意见: " + this.recommendation
                + "\n缺陷部位: " + this.defectPart
                + "\n缺陷原因: " + this.defectReason
                + "\n解决方案: " + this.solution
                + "\n备注: " + this.comments;
    }

    //~ Getter methods ---------------------------------------------------------

    public String getApperance() {
        return this.apperance;
    }
    public Document getDescription() {
        return this.description;
    }
    public String getRecommendation() {
        return this.recommendation;
    }
    public String getDefectPart() {
        return this.defectPart;
    }
    public String getDefectReason() {
        return this.defectReason;
    }
    public String getSolution() {
        return this.solution;
    }
    public String getComments() {
        return this.comments;
    }

    //~ Builder ----------------------------------------------------------------

    public static class DetailsBuilder {
        private final String _apperance; // 缺陷表象
        private final Document _description; // 缺陷描述
        private String _recommendation; // 处理意见
        private String _defectPart; // 缺陷部位
        private String _defectReason; // 缺陷原因
        private String _solution; // 解决方案
        private String _comments; // 备注

        public DetailsBuilder(String apperance, Document description) {
            this._apperance = apperance;
            this._description = description;
        }

        public DetailsBuilder recommendation(String recommendation) {
            this._recommendation = recommendation;
            return this;
        }
        public DetailsBuilder defectPart(String defectPart) {
            this._defectPart = defectPart;
            return this;
        }
        public DetailsBuilder defectReason(String defectReason) {
            this._defectReason = defectReason;
            return this;
        }
        public DetailsBuilder solution(String solution) {
            this._solution = solution;
            return this;
        }
        public DetailsBuilder comments(String comments) {
            this._comments = comments;
            return this;
        }

        public Details createDetails() {
            return new Details(_apperance, _description, _recommendation, _defectPart, _defectReason, _solution, _comments);
        }
    }
}

// End Details.java
