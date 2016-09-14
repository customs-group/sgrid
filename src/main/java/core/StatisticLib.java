package core;

import core.columnGroups.*;
import edwardlol.*;

import java.io.*;
import java.math.BigInteger;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * Created by edwardlol on 16/5/25.
 */
public class StatisticLib extends AbstractListLib<DefectComplete> {

    private Map<String, List<DefectComplete>> locationMap = new HashMap<>(); // location : defectList

    public StatisticLib() { }

    public void initFromFile(String file) {
        long startTime = System.currentTimeMillis();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine(); // the first line is column name
            line = bufferedReader.readLine();
            while (line != null) {
                String[] contents = line.split(",");
                BigInteger id = new BigInteger(Utility.readStringWithNull(contents[2]));
                String company = Utility.readStringWithNull(contents[3]);
                String department = Utility.readStringWithNull(contents[4]);
                String location = Utility.readStringWithNull(contents[5]);
                String defectLevel = Utility.readStringWithNull(contents[6]);
                String type = Utility.readStringWithNull(contents[7]);
                String classification = Utility.readStringWithNull(contents[8]);
                String equipName = Utility.readStringWithNull(contents[9]);
                String equipType = Utility.readStringWithNull(contents[10]);
                String functionPosition = Utility.readStringWithNull(contents[11]);
                String partsName = Utility.readStringWithNull(contents[12]);
                String voltageString = Utility.readStringWithNull(contents[13]);
                Calendar findDate = Utility.stringToCalendar(contents[14]);
                String defectApperance = Utility.readStringWithNull(contents[15]);
                String defectDescription = Utility.readStringWithNull(contents[16]);
                String defectType = Utility.readStringWithNull(contents[17]);
                // TODO: 16/6/12 defectClass: the added column
                String defectClass = Utility.readStringWithNull(contents[18]);
                Calendar reportDate = Utility.stringToCalendar(contents[19]);
                Calendar solveDate = Utility.stringToCalendar(contents[20]);
                String recommendation = Utility.readStringWithNull(contents[21]);
                String manufactor = Utility.readStringWithNull(contents[22]);
                String model = Utility.readStringWithNull(contents[23]);
                String defectReason = Utility.readStringWithNull(contents[24]);
                String defectPart = Utility.readStringWithNull(contents[25]);
                // 26 解决方案
                String defectStatus = Utility.readStringWithNull(contents[27]);
                // 28 设备生产日期
                Calendar operationDate = Utility.stringToCalendar(contents[29]);

                int voltage;
                Pattern vPattern = Pattern.compile("(\\d+)[vV]");
                Pattern kvPattern = Pattern.compile("(\\d+)[kK][vV]");
                Matcher vMatcher = vPattern.matcher(voltageString);
                Matcher kvMatcher = kvPattern.matcher(voltageString);
                if (vMatcher.find()) {
                    voltage = Integer.parseInt(vMatcher.group(1)) / 1000;
                } else if (kvMatcher.find()) {
                    voltage = Integer.parseInt(kvMatcher.group(1));
                } else {
                    voltage = 0;
                }

                Document _defectDescription = new Document(defectDescription);

                Overview overview = new Overview.OverviewBuilder(defectLevel, defectType,
                        defectClass, defectStatus).createOverview();

                Details details = new Details.DetailsBuilder(defectApperance, _defectDescription)
                        .defectReason(defectReason).recommendation(recommendation).createDetails();

                DateInfo dateInfo = new DateInfo.DateInfoBuilder(reportDate, operationDate)
                        .findDate(findDate).solvedDate(solveDate).createDateInfo();

                BelongingInfo belongingInfo = new BelongingInfo.BelongingInfoBuilder(company,
                        department, location, manufactor).createBelongingInfo();

                EquipInfo equipInfo = new EquipInfo.EquipInfoBuilder(equipName, equipType, voltage)
                        .functionPosition(functionPosition).partsName(partsName).model(model).createEquipInfo();

                DefectComplete defect = new DefectComplete.Builder().overview(overview).details(details)
                        .dateInfo(dateInfo).equipInfo(equipInfo).belongingInfo(belongingInfo).build();
//                defect.id = id;
                this.elements.add(defect);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long finishTime = System.currentTimeMillis();
        System.out.println("data reading finished in " + (finishTime - startTime) / 1000.0 + " seconds");
    }

    /**
     * 按照地点进行分类,之后分别统计在时间阈值interval内的fieldName相关联性
     * @param fieldName
     * @param interval
     * @param file
     */
    public void checkDateCorrelation(String fieldName, int interval, String file) {
        this.elements.forEach(defect -> {
            String location = defect.getLocation();
            List<DefectComplete> list = this.locationMap.containsKey(location) ? this.locationMap.get(location) : new ArrayList<>();
            list.add(defect);
            this.locationMap.put(location, list);
        });

        this.locationMap.forEach((location, defectList) -> {
            // sort every entry of location map according to the report date
            Collections.sort(defectList, (o1, o2) -> {
                int x = Utility.getIntervalDays(o1.getReportDate(), o2.getReportDate());
                return (x < 0) ? -1 : ((x == 0) ? 0 : 1);
            });

            Map<String, Integer> correlationMap = new HashMap<>();
            for (int i = 0; i < defectList.size(); i++) {
                DefectComplete defecti = defectList.get(i);
                String fieldi = defecti.getField(fieldName);
                for (int j = i + 1; j < defectList.size(); j++) {
                    DefectComplete defectj = defectList.get(j);
                    String fieldj = defectj.getField(fieldName);
                    if (Math.abs(Utility.getIntervalDays(defecti.getReportDate(), defectj.getReportDate())) <= interval
                            && !fieldi.equals("无")
                            && !fieldj.equals("无")) {
                        Utility.updateCountMap(correlationMap, fieldi + " -> " + fieldj);
                    }
                }
            }
            // write result to file
            try {
                String path = file + DefectComplete.getName(fieldName) + "/" + interval;
                File fp = new File(path);
                fp.mkdirs();
                FileWriter fileWriter = new FileWriter(path + "/" + location + ".txt");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("相关联" + DefectComplete.getName(fieldName) + "\t:\t时间阈值内发生次数\n");
                bufferedWriter.write("-------------------------------\n");
                bufferedWriter.flush();
                correlationMap.forEach((_key, _value) -> {
                    try {
                        bufferedWriter.write(_key + "\t:\t" + _value + "\n");
                        bufferedWriter.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                bufferedWriter.close();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * record all the defects information
     * sorted by department
     * @param file output file
     */
    public void recordStatisticResult(String file) {
        Collections.sort(this.elements, (DefectComplete defect1, DefectComplete defect2) ->
                Collator.getInstance(java.util.Locale.CHINA).compare(
                        defect1.getDepartment(), defect2.getDepartment()));
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            this.elements.forEach(defect -> {
                try {
                    bufferedWriter.write(defect.toString() + ",\n");
                    bufferedWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            bufferedWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get a global view of the defect class and level
     */
    public void globalStastic(String file) {
        Map<String, Integer> classMap = new HashMap<>();
        Map<String, Integer> levelMap = new HashMap<>();

        this.elements.forEach(defect -> {
            int classCount = 1, levelCount = 1;
            if (classMap.containsKey(defect.getDefectClass())) {
                classCount += classMap.get(defect.getDefectClass());
            }
            classMap.put(defect.getDefectClass(), classCount);
            if (levelMap.containsKey(defect.getLevel())) {
                levelCount += levelMap.get(defect.getLevel());
            }
            levelMap.put(defect.getLevel(), levelCount);
        });

        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Map.Entry<String, Integer> entry : classMap.entrySet()) {
                bufferedWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            bufferedWriter.write("\n");
            bufferedWriter.flush();
            for (Map.Entry<String, Integer> entry : levelMap.entrySet()) {
                bufferedWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分别统计每个生产厂家的故障数据
     * 按照设备正常运行的月数group by 统计缺陷发生次数
     * 用于echarts展示
     * @param file
     * @return
     */
    public Map<String, Map<Integer, Integer>> manufactorMonth(String file) {
        Map<String, Map<Integer, Integer>> manufactorMap = new HashMap<>();
        this.elements.forEach(defect -> {
            int operationMonths = defect.getOperationYears();
            if (operationMonths < 300 && operationMonths >= 0) {
                Map<Integer, Integer> cntMap;
                if (manufactorMap.containsKey(defect.getManufactor())) { // 生产厂家存在map中
                    cntMap = manufactorMap.get(defect.getManufactor());
                    Utility.updateCountMap(cntMap, operationMonths);
                } else {
                    cntMap = new HashMap<>();
                    cntMap.put(operationMonths, 1);
                }
                manufactorMap.put(defect.getManufactor(), cntMap);
            }

        });
        // record to file
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            manufactorMap.forEach((manufactor, monthMap) -> {
                monthMap.forEach((month, count) -> {
                    try {
                        bufferedWriter.write("[" + month + "," + count + "," + "\"" + manufactor + "\"" + "]," + "\n");
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            });
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manufactorMap;
    }

    /**
     * 分组统计每年故障发生的次数
     * 用于echarts展示
     * @param file
     * @return
     */
    public Map<Integer, Integer> getYearCount(String file) {
        Map<Integer, Integer> yearCount = new HashMap<>();
        this.elements.forEach(defect -> {
            int cnt = 1;
            if (yearCount.containsKey(defect.getOperationYears())) {
                cnt += yearCount.get(defect.getOperationYears());
            }
            yearCount.put(defect.getOperationYears(), cnt);
        });
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            yearCount.forEach((year, count) -> {
                try {
                    bufferedWriter.write("[" + year + "," + count + "],");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yearCount;
    }


    public void getPartAndClass() {
        // initFromCSVFile
        Set<String> catagoryIndex = new LinkedHashSet<>();
        Set<String> seriesIndex = new LinkedHashSet<>();
        this.elements.forEach(defect -> {
            catagoryIndex.add(defect.getDefectPart());
            seriesIndex.add(defect.getLevel());
        });
        // the big map1, [series:catagory:count]
        Map<String, Map<String, Integer>> seriesCatagoryCount = new HashMap<>();
        // initFromCSVFile
        for (String serie : seriesIndex) {
            Map<String, Integer> catagoryCount = new HashMap<>();
            for (String location : catagoryIndex) {
                catagoryCount.put(location, 0);
            }
            seriesCatagoryCount.put(serie, catagoryCount);
        }
        // put in data
        this.elements.forEach(defect -> {
            String catagory = defect.getDefectPart();
            String serie = defect.getLevel();
            Map<String, Integer> catagoryCount = seriesCatagoryCount.get(serie);
            int cnt = catagoryCount.get(catagory) + 1;
            catagoryCount.put(catagory, cnt);
            seriesCatagoryCount.put(serie, catagoryCount);
        });

        for (String serie : seriesIndex) {
            try {
                FileWriter fileWriter = new FileWriter("results/parts/level/" + serie + ".txt");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                Map<String, Integer> catagoryCount = seriesCatagoryCount.get(serie);
                bufferedWriter.write("[");
                for (String catagory : catagoryIndex) {
                    bufferedWriter.write(catagoryCount.get(catagory) + ",");
                }
                bufferedWriter.write("]\n");
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // write catagory index
        try {
            FileWriter fileWriter = new FileWriter("results/level/catagory.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("[");
            for (String catagory : catagoryIndex) {
                bufferedWriter.write("'" + catagory + "'" + ",");
            }
            bufferedWriter.write("]");
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * demo1
     * 缺陷类别->缺陷原因->count
     * @param path
     */
    public void demo1(String path) {
        Map<String, Map<String, List<String>>> map1 = new HashMap<>();
        this.elements.forEach(defect -> {
            Map<String, List<String>> map2 = map1.containsKey(defect.getDefectType()) ?
                    map1.get(defect.getDefectType()) : new HashMap<>();
            List<String> list = map2.containsKey(defect.getDefectReason()) ?
                    map2.get(defect.getDefectReason()) : new ArrayList<>();
            list.add(defect.getRecommendation());
            map2.put(defect.getDefectReason(), list);
            map1.put(defect.getDefectType(), map2);
        });

        try {
            FileWriter globalfw = new FileWriter(path + "_缺陷类别.txt");
            BufferedWriter globalbw = new BufferedWriter(globalfw);
            int totalCount = 0;
            for (Map.Entry<String, Map<String, List<String>>> entry1 : map1.entrySet()) {
                String key1 = entry1.getKey();
                Map<String, List<String>> map2 = entry1.getValue();

                File file1 = new File(path + key1);
                file1.mkdirs();

                FileWriter fw1 = new FileWriter(path + key1 + "/_缺陷原因.txt");
                BufferedWriter bw1 = new BufferedWriter(fw1);

                int count = 0;
                for (Map.Entry<String, List<String>> entry2 : map2.entrySet()) {
                    bw1.write(entry2.getKey() + ":" + entry2.getValue().size() + "\n");
                    bw1.flush();
                    String key2 = entry2.getKey();
                    List<String> list = entry2.getValue();

                    FileWriter fw2 = new FileWriter(path + key1 + "/" + key2 + ".txt");
                    BufferedWriter bw2 = new BufferedWriter(fw2);

                    for (String content : list) {
                        bw2.write(content + "\n");
                        bw2.flush();
                    }
                    bw2.close();
                    fw2.close();

                    count += entry2.getValue().size();
                }
                bw1.close();
                fw1.close();
                globalbw.write(entry1.getKey() + ": " + count + "\n");
                totalCount += count;

                globalbw.flush();
            }
            System.out.println("demo1 count: " + totalCount);
            globalbw.close();
            globalfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * demo2.1
     * 缺陷种类->缺陷类别->缺陷部位->count
     * @param path
     */
    public void demo2_1(String path) {
        Map<String, Map<String, Map<String, Integer>>> map1 = new HashMap<>();
        this.elements.forEach(defect -> {
            Map<String, Map<String, Integer>> map2 = map1.containsKey(defect.getDefectClass()) ?
                    map1.get(defect.getDefectClass()) : new HashMap<>();
            Map<String, Integer> map3 = map2.containsKey(defect.getDefectType()) ?
                    map2.get(defect.getDefectType()) : new HashMap<>();

            Utility.updateCountMap(map3, defect.getPartsName());

            map2.put(defect.getDefectType(), map3);
            map1.put(defect.getDefectClass(), map2);
        });

        try {
            FileWriter globalfw = new FileWriter(path + "_缺陷种类.txt");
            BufferedWriter globalbw = new BufferedWriter(globalfw);
            int totalcount = 0;
            for (Map.Entry<String, Map<String, Map<String, Integer>>> entry1 : map1.entrySet()) {
                String key1 = entry1.getKey();
                Map<String, Map<String, Integer>> map2 = entry1.getValue();

                File file1 = new File(path + key1);
                file1.mkdirs();

                FileWriter fw1 = new FileWriter(path + key1 + "/_缺陷类别.txt");
                BufferedWriter bw1 = new BufferedWriter(fw1);
                int count1 = 0;
                for (Map.Entry<String, Map<String, Integer>> entry2 : map2.entrySet()) {
                    String key2 = entry2.getKey();
                    Map<String, Integer> map3 = entry2.getValue();
                    int count2 = 0;

                    FileWriter fw2 = new FileWriter(path + key1 + "/" + key2 + ".txt");
                    BufferedWriter bw2 = new BufferedWriter(fw2);
                    for (Map.Entry<String, Integer> entry3 : map3.entrySet()) {
                        Integer count3 = entry3.getValue();
                        bw2.write(entry3.getKey() + ":" + count3 + "\n");
                        bw2.flush();
                        count2 += count3;
                    }
                    bw2.close();
                    fw2.close();

                    bw1.write(key2 + ": " + count2 + "\n");
                    bw1.flush();
                    count1 += count2;
                }
                bw1.close();
                fw1.close();

                globalbw.write(entry1.getKey() + ": " + count1 + "\n");
                totalcount += count1;

                globalbw.flush();
            }
            System.out.println("demo2 count: " + totalcount);
            globalbw.close();
            globalfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * demo2.2
     * 缺陷等级->生产厂家->从报告缺陷到消除缺陷花费的小时数
     * @param path
     */
    public void demo2_2(String path) {
        // 1
        Map<String, Integer> manuMap = new HashMap<>();
        this.elements.forEach(defect -> Utility.updateCountMap(manuMap, defect.getManufactor()));
        try {
            FileWriter fw = new FileWriter("./results/demo/2_2/1.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (Map.Entry<String, Integer> entry : manuMap.entrySet()) {
                bw.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2
        Map<String, Set<Float>> _departmentMap = new HashMap<>();
        this.elements.forEach(defect -> {
            Set<Float> set = _departmentMap.containsKey(defect.getDepartment()) ?
                    _departmentMap.get(defect.getDepartment()) : new HashSet<>();
            if (defect.getSolveHours() != 0) {
                set.add(defect.getSolveHours() * 1.0f);
            }
            _departmentMap.put(defect.getDepartment(), set);
        });

        Map<String, Float> departmentMap = new HashMap<>();
        _departmentMap.forEach((department, set) -> {
            float sum = 0f;
            for (Float item : set) {
                sum += item;
            }
            sum /= set.size();
            departmentMap.put(department, sum);
        });
        try {
            FileWriter fw = new FileWriter("./results/demo/2_2/2.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            for (Map.Entry<String, Float> entry : departmentMap.entrySet()) {
                bw.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
