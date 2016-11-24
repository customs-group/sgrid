package core;

import java.math.BigInteger;
import java.util.*;

import core.columnGroups.*;

/**
 *
 * Created by edwardlol on 16/5/25.
 */
public class DefectComplete extends DefectBase {

    //~ Instance fields --------------------------------------------------------

    private BigInteger id; // not using

    // components of defect in groups
    // see core.columnGroups
    private final Overview overview;
    private final Details details;
    private final DateInfo dateInfo;
    private final EquipInfo equipInfo;
    private final BelongingInfo belongingInfo;

    //~ Constructors -----------------------------------------------------------

    /** private constructer
     * use Builder to get an instance of this class
     */
    private DefectComplete(Overview overview, Details details, DateInfo dateInfo,
                           EquipInfo equipInfo, BelongingInfo belongingInfo) {
        this.overview = overview;
        this.details = details;
        this.dateInfo = dateInfo;
        this.equipInfo = equipInfo;
        this.belongingInfo = belongingInfo;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * a String contains everything of this defect
     * @return the complete description of this defect in String
     */
    @Override
    public String toString() {
        String result = "[";
        result += this.getOperationYears();
        result += ",";
        result += this.getVoltage();
        result += ",";
        result += "\"" + this.getDepartment() + "\"";
        result += ",";
        result += "\"" + this.getLocation() + "\"";
        result += ",";
        result += "\"" + this.getLevel() + "\"";
        result += ",";
        result += "\"" + this.getType() + "\"";
        result += ",";
        result += "\"" + this.getEquipType() + "\"";
        result += ",";
        result += "\"" + this.getFunctionPosition() + "\"";
        result += ",";
        result += "\"" + this.getPartsName() + "\"";
        result += ",";
        result += "\"" + this.getDefectType() + "\"";
        result += ",";
        result += "\"" + this.getDefectClass() + "\"";
        result += ",";
        result += "\"" + this.getManufactor() + "\"";
        result += ",";
        result += "\"" + this.getEquipModel() + "\"";
        result += ",";
        result += "\"" + this.getStatus() + "\"";
        result += "]";
        return result;
    }

    //~ Getter methods ---------------------------------------------------------

    // overview
    public Overview getOverview() {
        return this.overview;
    }
    public String getLevel() {
        return this.overview.getLevel();
    }
    public String getDefectType() {
        return this.overview.getDefectType();
    }
    public String getDefectClass() {
        return this.overview.getDefectClass();
    }
    public String getStatus() {
        return this.overview.getStatus();
    }
    public String getType() {
        return this.overview.getType();
    }
    public String getClassification() {
        return this.overview.getClassification();
    }
    // details
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
    // dateInfo
    public DateInfo getDateInfo() {
        return this.dateInfo;
    }
    public Calendar getReportDate() {
        return this.dateInfo.getReportDate();
    }
    public Calendar getOperationDate() {
        return this.dateInfo.getOperationDate();
    }
    public Calendar getFindDate() {
        return this.dateInfo.getFindDate();
    }
    public Calendar getSolveDate() {
        return this.dateInfo.getSolveDate();
    }
    public Calendar getProducedDate() {
        return this.dateInfo.getProducedDate();
    }
    public int getOperationDays() {
        return this.dateInfo.getOperationDays();
    }
    public int getOperationMonths() {
        return this.dateInfo.getOperationMonths();
    }
    public int getOperationYears() {
        return this.dateInfo.getOperationYears();
    }
    public int getSolveHours() {
        return this.dateInfo.getSolveHours();
    }
    // equipInfo
    public EquipInfo getEquipInfo() {
        return this.equipInfo;
    }
    public String getEquipName() {
        return this.equipInfo.getName();
    }
    public String getEquipType() {
        return this.equipInfo.getType();
    }
    public int getVoltage() {
        return this.equipInfo.getVoltage();
    }
    public String getFunctionPosition() {
        return this.equipInfo.getFunctionPosition();
    }
    public String getPartsName() {
        return this.equipInfo.getPartsName();
    }
    public String getEquipModel() {
        return this.equipInfo.getModel();
    }
    // belongingInfo
    public BelongingInfo getBelongingInfo() {
        return this.belongingInfo;
    }
    public String getCompany() {
        return this.belongingInfo.getCompany();
    }
    public String getDepartment() {
        return this.belongingInfo.getDepartment();
    }
    public String getLocation() {
        return this.belongingInfo.getLocation();
    }
    public String getManufactor() {
        return this.belongingInfo.getManufactor();
    }

    //~ Builder ----------------------------------------------------------------

    public static class Builder {
        private Overview _overview;
        private Details _details;
        private DateInfo _dateInfo;
        private EquipInfo _equipInfo;
        private BelongingInfo _belongingInfo;

        public Builder() {}
        public Builder overview(Overview overview) {
            this._overview = overview;
            return this;
        }
        public Builder details(Details details) {
            this._details = details;
            return this;
        }
        public Builder dateInfo(DateInfo dateInfo) {
            this._dateInfo = dateInfo;
            return this;
        }
        public Builder equipInfo(EquipInfo equipInfo) {
            this._equipInfo = equipInfo;
            return this;
        }
        public Builder belongingInfo(BelongingInfo belongingInfo) {
            this._belongingInfo = belongingInfo;
            return this;
        }

        public DefectComplete build() {
            return new DefectComplete(_overview, _details, _dateInfo, _equipInfo, _belongingInfo);
        }
    }


}

// End DefectComplete.java
