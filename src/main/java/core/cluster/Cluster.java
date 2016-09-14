package core.cluster;

import core.DefectComplete;
import edwardlol.*;

import java.util.*;

/**
 *
 * Created by edwardlol on 16/6/10.
 */
class Cluster extends AbstractCluster<DefectComplete> {

    /**
     * get the lable of this cluster
     * @param labelType specify the field used to represent as the label
     *                  e.g. reportDate, solveHours, etc.
     * @return label in double, 1.0, -1.0, etc.
     */
    public double getLabel(String labelType) {
        switch (labelType.toLowerCase()) {
            case "days": return getDaysAsLabel();
            case "hours": return getHoursAsLabel();
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * get the operation days as label
     * @return average operation days of this cluster
     */
    private double getDaysAsLabel() {
        double result = 0.0d;
        int count = 0;
        for (DefectComplete defect : this.elements) {
            if (defect.getOperationDays() != 0) {
                result += defect.getOperationDays();
                count++;
            }
        }
        return result / count;
    }

    /**
     * get the hour in day of report date as label
     * @return average hour in day of this cluster
     */
    private double getHoursAsLabel() {
        double result = 0.0d;
        int count = 0;
        for (DefectComplete defect : elements) {
            if (defect.getReportDate() != null) {
                Calendar calendar = defect.getReportDate();
                result += calendar.get(Calendar.HOUR_OF_DAY);
                count++;
            }
        }
        return result / count;
    }
}
