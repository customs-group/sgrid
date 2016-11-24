package core.columnGroups;

import java.io.Serializable;

/**
 *
 * Created by edwardlol on 16/5/29.
 */
public class EquipInfo implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private final String name; // 设备名称
    private final String equipType; // 设备类别
    private final int voltage; // 电压等级
    private final String functionPosition; // 功能位置
    private final String partsName; // 部件名称
    private final String model; // 设备型号

    //~ Constructors -----------------------------------------------------------

    /** private constructer
     * use Builder to get an instance of this class
     */
    private EquipInfo(String name, String equipType, int voltage, String functionPosition, String partsName, String model) {
        this.name = name;
        this.equipType = equipType;
        this.voltage = voltage;
        this.functionPosition = functionPosition;
        this.partsName = partsName;
        this.model = model;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String toString() {
        return "设备名称: " + this.name
                + "\n设备类别: " + this.equipType
                + "\n电压等级: " + this.voltage
                + "\n功能位置: " + this.functionPosition
                + "\n部件名称: " + this.partsName
                + "\n设备型号: " + this.model;
    }

    //~ Getter methods ---------------------------------------------------------

    public String getName() {
        return this.name;
    }
    public String getEquipType() {
        return this.equipType;
    }
    public int getVoltage() {
        return this.voltage;
    }
    public String getFunctionPosition() {
        return this.functionPosition;
    }
    public String getPartsName() {
        return this.partsName;
    }
    public String getModel() {
        return this.model;
    }

    //~ Builder ----------------------------------------------------------------

    public static class EquipInfoBuilder {
        private final String _name; // 设备名称
        private final String _equipType; // 设备类别
        private final int _voltage; // 电压等级
        private String _functionPosition; // 功能位置
        private String _partsName; // 部件名称
        private String _model; // 设备型号

        public EquipInfoBuilder(String name, String equipType, int voltage) {
            this._name = name;
            this._equipType = equipType;
            this._voltage = voltage;
        }
        public EquipInfoBuilder functionPosition(String functionPosition) {
            this._functionPosition = functionPosition;
            return this;
        }
        public EquipInfoBuilder partsName(String partsName) {
            this._partsName = partsName;
            return this;
        }
        public EquipInfoBuilder model(String model) {
            this._model = model;
            return this;
        }

        public EquipInfo createEquipInfo() {
            return new EquipInfo(_name, _equipType, _voltage, _functionPosition, _partsName, _model);
        }
    }
}

// End EquipInfo.java
