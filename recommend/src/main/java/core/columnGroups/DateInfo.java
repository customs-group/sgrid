package core.columnGroups;

import util.*;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * Created by edwardlol on 16/5/29.
 */
public class DateInfo implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private final Calendar reportDate; // 报告时间
    private final Calendar operationDate; // 设备投产日期
    private final Calendar findDate; // 发现时间
    private final Calendar solveDate; // 消缺时间
    private final Calendar producedDate; // 设备生产日期

    private int operationDays; // 到报告缺陷为止设备的正常运行天数
    private int operationMonths; // 到报告缺陷为止设备的正常运行月数
    private int operationYears; // 到报告缺陷为止设备的正常运行年数
    private int solveHours; // 从报告缺陷到消除缺陷花费的小时数

    //~ Constructors -----------------------------------------------------------

    /** private constructer
     * use Builder to get an instance of this class
     */
    private DateInfo(Calendar reportDate, Calendar operationDate, Calendar findDate,
                     Calendar solveDate, Calendar producedDate) {
        this.reportDate = reportDate;
        this.operationDate = operationDate;
        this.findDate = findDate;
        this.solveDate = solveDate;
        this.producedDate = producedDate;

        if (this.reportDate != null && this.operationDate != null) {
            this.operationDays = Util.getIntervalDays(this.reportDate, this.operationDate);
            this.operationMonths = this.operationDays / 30;
            this.operationYears = this.operationDays / 365;
        }

        if (this.findDate != null && this.solveDate != null) {
            this.solveHours = Util.getIntervalHours(this.solveDate, this.findDate);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String toString() {
        return "设备生产日期: " + this.producedDate.toString()
                + "\n设备投产日期: " + this.operationDate.toString()
                + "\n发现时间: " + this.findDate.toString()
                + "\n报告时间: " + this.reportDate.toString()
                + "\n消缺时间: " + this.solveDate.toString();
    }

    //~ Getter methods ---------------------------------------------------------

    public Calendar getReportDate() {
        return this.reportDate;
    }
    public Calendar getOperationDate() {
        return this.operationDate;
    }
    public Calendar getFindDate() {
        return this.findDate;
    }
    public Calendar getSolveDate() {
        return this.solveDate;
    }
    public Calendar getProducedDate() {
        return this.producedDate;
    }
    public int getOperationDays() {
        return this.operationDays;
    }
    public int getOperationMonths() {
        return this.operationMonths;
    }
    public int getOperationYears() {
        return this.operationYears;
    }
    public int getSolveHours() {
        return this.solveHours;
    }

    //~ Builder ----------------------------------------------------------------

    public static class DateInfoBuilder {
        private final Calendar _reportDate; // 报告时间
        private final Calendar _operationDate; // 设备投产日期
        private Calendar _findDate; // 发现时间
        private Calendar _solveDate; // 消缺时间
        private Calendar _producedDate; // 设备生产日期

        public DateInfoBuilder(Calendar reportDate, Calendar operationDate) {
            this._reportDate = reportDate;
            this._operationDate = operationDate;
        }

        public DateInfoBuilder findDate(Calendar findDate) {
            this._findDate = findDate;
            return this;
        }
        public DateInfoBuilder solvedDate(Calendar solveDate) {
            this._solveDate = solveDate;
            return this;
        }
        public DateInfoBuilder producedDate(Calendar producedDate) {
            this._producedDate = producedDate;
            return this;
        }

        public DateInfo createDateInfo() {
            return new DateInfo(_reportDate, _operationDate, _findDate, _solveDate, _producedDate);
        }
    }
}

// End DateInfo.java
