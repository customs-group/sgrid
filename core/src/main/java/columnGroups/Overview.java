package core.columnGroups;

import java.io.Serializable;

/**
 *
 * Created by edwardlol on 16/5/29.
 */
public class Overview implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private final String level; // 严重等级
    private final String defectType; // 缺陷类别
    private final String defectClass; // 缺陷种类
    private final String status; // 状态
    private final String type; // 类别
    private final String classification; // 分类

    //~ Constructors -----------------------------------------------------------

    /** private constructer
     * use Builder to get an instance of this class
     */
    private Overview(String level, String defectType, String defectClass,
                     String status, String type, String classification) {
        this.level = level;
        this.defectType = defectType;
        this.defectClass = defectClass;
        this.type = type;
        this.classification = classification;
        this.status = status;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String toString() {
        return "严重等级: " + this.level
                + "\n缺陷类别: " + this.defectType
                + "\n缺陷种类: " + this.defectClass
                + "\n状态: " + this.status
                + "\n类别: " + this.type
                + "\n分类: " + this.classification;
    }

    //~ Getter methods ---------------------------------------------------------

    public String getLevel() {
        return this.level;
    }
    public String getDefectType() {
        return this.defectType;
    }
    public String getDefectClass() {
        return this.defectClass;
    }
    public String getStatus() {
        return this.status;
    }
    public String getType() {
        return this.type;
    }
    public String getClassification() {
        return this.classification;
    }

    //~ Builder ----------------------------------------------------------------

    public static class OverviewBuilder {
        private final String _level; // 严重等级
        private final String _defectType; // 缺陷类别
        private final String _defectClass; // 缺陷种类
        private final String _status; // 状态
        private String _type; // 类别
        private String _classification; // 分类

        public OverviewBuilder(String level, String defectType, String defectClass, String status) {
            this._level = level;
            this._defectType = defectType;
            this._defectClass = defectClass;
            this._status = status;
        }

        public OverviewBuilder type(String type) {
            this._type = type;
            return this;
        }
        public OverviewBuilder classification(String classification) {
            this._classification = classification;
            return this;
        }

        public Overview createOverview() {
            return new Overview(_level, _defectType, _defectClass, _status, _type, _classification);
        }
    }
}

// End Overview.java
