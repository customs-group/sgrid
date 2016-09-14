package core;

import core.columnGroups.*;

/**
 *
 * Created by edwardlol on 16/9/14.
 */
public class DefectSimple extends DefectBase {

    //~ Instance fields --------------------------------------------------------

    private final Details details;

    //~ Constructors -----------------------------------------------------------

    /** private constructer
     * use Builder to get an instance of this class
     */
    private DefectSimple(Details details) {
        this.details = details;
    }

    //~ Getter methods ---------------------------------------------------------

    public Details getDetails() {
        return this.details;
    }
    public String getApperance() {
        return this.details.getApperance();
    }
    public Document getDescription() {
        return this.details.getDescription();
    }
    public String getRecommendation() {
        return this.details.getRecommendation();
    }
    public String getDefectPart() {
        return this.details.getDefectPart();
    }
    public String getDefectReason() {
        return this.details.getDefectReason();
    }
    public String getSolution() {
        return this.details.getSolution();
    }
    public String getComments() {
        return this.details.getComments();
    }

    //~ Builder ----------------------------------------------------------------

    public static class Builder {
        private Details _details;

        public Builder() {}

        public Builder details(Details details) {
            this._details = details;
            return this;
        }

        public DefectSimple build() {
            return new DefectSimple(_details);
        }
    }

}

// End DefectSimple.java
