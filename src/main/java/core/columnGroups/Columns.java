package core.columnGroups;

/**
 * describe the columns and their field names
 * and real names. may be used in columnGroups
 * or in stasticLib for record the results
 * not finished
 * Created by edwardlol on 16/9/15.
 */
public enum Columns {

    level("严重等级"),
    defectType("缺陷类别"),
    defectClass("缺陷种类"),
    status("状态"),
    type("类别"),
    classification("分类"),
    apperance("缺陷表象"),
    description("缺陷描述"),
    recommendation("处理意见"),
    defectPart("缺陷部位"),
    defectReason("缺陷原因"),
    solution("解决方案"),
    comments("备注"),
    reportDate("报告时间"),
    operationDate("设备投产日期"),
    findDate("发现时间"),
    solveDate("消缺时间"),
    producedDate("设备生产日期"),
    name("设备名称"),
    equipType("设备类别"),
    voltage("电压等级"),
    functionPosition("功能位置"),
    partsName("部件名称"),
    model("设备型号"),
    company("公司"),
    department("单位"),
    location("地点"),
    manufactor("生产厂家");

    private String text;

    Columns(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

}
