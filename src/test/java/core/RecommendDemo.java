package core;

/**
 *
 * Created by edwardlol on 16/8/13.
 */
public class RecommendDemo {

    public static void main(String[] args) {

        RecommendLib recommendLib = RecommendLib.getInstance();

        recommendLib.initFromCSVFile("./datasets/defects/AllDefects.cleared.csv");


        // 监控后台报“百永甲线主一保护装置电源异常、"通道一、通道二告警”，现场检查500kV百永甲线主一保护装置“运行”灯灭，液晶面板无任何显示。
        recommendLib.recommend("监控后台报  主一保护装置", 5);

        // 35kV C312低抗C312断路器SF6压力低报警（C312开关当前SF6压力为0.505MPa，低于告警压力值0.52MPa）。
        recommendLib.recommend("C312低抗 C312断路器 SF6压力低报警", 2);

        // 计算机室6P同步时钟主机屏电力系统同步主时钟装置（#2）故障，电源空开跳闸。
        recommendLib.recommend("同步时钟装置故障 主机屏电力系统  电源跳闸", 3);

        // 监控系统操作站（1）显卡故障，无法显示，需返厂维修
        recommendLib.recommend("操作站 显卡 无法显示", 1);

        // 进行7#母线转检修时，2021617地刀不能电磁解锁。
        recommendLib.recommend("地刀  不能电磁解锁", 2);

    }
}
