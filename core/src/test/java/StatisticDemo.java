/**
 *
 * Created by edwardlol on 16/8/13.
 */
public class StatisticDemo {
    public static void main(String[] args) {
        StatisticLib statisticLib = StatisticLib.getInstance();
        statisticLib.initFromCSVFile("./datasets/defects/SwitchDefects.cleared.csv");

//        statisticLib.checkDateCorrelation("defectClass", 7, "./results/defects/correlation/");
//        statisticLib.manufactorMonth("./results/defects/manufactorMonth.txt");
//        statisticLib.demo1("./results/demo/1/");
//        statisticLib.demo2_1("./results/demo/2_1/");
//        statisticLib.demo2_2("./results/demo/2_2/");
    }
}
