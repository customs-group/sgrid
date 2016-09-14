package core;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * the base of all defects containing some basic fields and methods
 * Created by edwardlol on 16/9/14.
 */
public abstract class DefectBase implements Defect, Serializable {

    //~ Static fields/initializers ---------------------------------------------

    /**
     * a static dict that maps fields name to their real name
     */
    static final Map<String, String> fieldDict;

    static {
        fieldDict = new HashMap<>();
        fieldDict.put("level", "严重等级");
        fieldDict.put("defectType", "缺陷类别");
        fieldDict.put("defectClass", "缺陷种类");
        fieldDict.put("status", "状态");
        fieldDict.put("type", "类别");
        fieldDict.put("classification", "分类");

        fieldDict.put("apperance", "缺陷表象");
        fieldDict.put("description", "缺陷描述");
        fieldDict.put("recommendation", "处理意见");
        fieldDict.put("defectPart", "缺陷部位");
        fieldDict.put("defectReason", "缺陷原因");
        fieldDict.put("solution", "解决方案");
        fieldDict.put("comments", "备注");

        fieldDict.put("reportDate", "报告时间");
        fieldDict.put("operationDate", "设备投产日期");
        fieldDict.put("findDate", "发现时间");
        fieldDict.put("solveDate", "消缺时间");
        fieldDict.put("producedDate", "设备生产日期");

        fieldDict.put("name", "设备名称");
        fieldDict.put("type", "设备类别");
        fieldDict.put("voltage", "电压等级");
        fieldDict.put("functionPosition", "功能位置");
        fieldDict.put("partsName", "部件名称");
        fieldDict.put("model", "设备型号");

        fieldDict.put("company", "公司");
        fieldDict.put("department", "单位");
        fieldDict.put("location", "地点");
        fieldDict.put("manufactor", "生产厂家");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * get the real name from the field name
     * @param name the field name
     * @return the real name
     */
    public static String getName(String name) {
        return fieldDict.get(name);
    }

    /**
     * get the content of a String field by the field name
     * @param fieldName field name
     * @return the String content
     */
    public String getField(String fieldName) {
        String methodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
        try {
            java.lang.reflect.Method method = this.getClass().getMethod(methodName);
            return (String) method.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException | SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

}

// End DefectBase.java
