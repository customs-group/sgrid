package core.columnGroups;

import java.io.Serializable;

/**
 *
 * Created by edwardlol on 16/6/1.
 */
public class BelongingInfo implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private final String company; // 公司
    private final String department; // 单位
    private final String location; // 地点
    private final String manufactor; // 生产厂家

    //~ Constructors -----------------------------------------------------------

    /** private constructer
     * use Builder to get an instance of this class
     */
    private BelongingInfo(String company, String department, String location, String manufactor) {
        this.company = company;
        this.department = department;
        this.location = location;
        this.manufactor = manufactor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String toString() {
        return "公司: " + this.company
                + "\n单位: " + this.department
                + "\n地点: " + this.location
                + "\n生产厂家: " + this.manufactor;
    }

    //~ Getter methods ---------------------------------------------------------

    public String getCompany() {
        return this.company;
    }
    public String getDepartment() {
        return this.department;
    }
    public String getLocation() {
        return this.location;
    }
    public String getManufactor() {
        return this.manufactor;
    }

    //~ Builder ----------------------------------------------------------------

    public static class BelongingInfoBuilder {
        private final String _company; // 公司
        private final String _department; // 单位
        private final String _location; // 地点
        private final String _manufactor; // 生产厂家

        public BelongingInfoBuilder(String company, String department, String location, String manufactor) {
            this._company = company;
            this._department = department;
            this._location = location;
            this._manufactor = manufactor;
        }

        public BelongingInfo createBelongingInfo() {
            return new BelongingInfo(_company, _department, _location, _manufactor);
        }
    }
}

// End BelongingInfo.java
