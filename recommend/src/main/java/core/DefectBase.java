package core;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * the base of all defects containing some basic fields and methods
 * Created by edwardlol on 16/9/14.
 */
abstract class DefectBase implements Defect, Serializable {

    //~ Static fields/initializers ---------------------------------------------

    /**
     * a static dict that maps fields name to their real name
     */
    static final ImmutableMap<String, String> fieldDict = ImmutableMap.<String, String>builder()
            .put("level", "严重等级")
            .put("defectType", "缺陷类别")
            .put("defectClass", "缺陷种类")
            .put("status", "状态")
            .put("type", "类别")
            .put("classification", "分类")
            .put("apperance", "缺陷表象")
            .put("description", "缺陷描述")
            .put("recommendation", "处理意见")
            .put("defectPart", "缺陷部位")
            .put("defectReason", "缺陷原因")
            .put("solution", "解决方案")
            .put("comments", "备注")
            .put("reportDate", "报告时间")
            .put("operationDate", "设备投产日期")
            .put("findDate", "发现时间")
            .put("solveDate", "消缺时间")
            .put("producedDate", "设备生产日期")
            .put("name", "设备名称")
            .put("equipType", "设备类别")
            .put("voltage", "电压等级")
            .put("functionPosition", "功能位置")
            .put("partsName", "部件名称")
            .put("model", "设备型号")
            .put("company", "公司")
            .put("department", "单位")
            .put("location", "地点")
            .put("manufactor", "生产厂家")
            .build();

    //~ Methods ----------------------------------------------------------------

    /**
     * get the real name from the field name
     * @param name the field name
     * @return the real name
     */
    @SuppressWarnings("unused")
    public static String getName(String name) {
        return fieldDict.get(name);
    }

    /**
     * get the content of a String field by the field name
     * @param fieldName field name
     * @return the String content
     */
    @SuppressWarnings("unused")
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
