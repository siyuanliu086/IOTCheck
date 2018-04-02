package com.iss.iotcheck;

import java.util.List;

import com.iss.iotcheck.model.OlMonitorMinData;
import com.iss.iotcheck.model.OlMonitorWaterData;
import com.iss.iotcheck.plugin.IProcessing;
import com.iss.iotcheck.plugin.NationalStandard212;
import com.iss.iotcheck.plugin.Process212;
import com.iss.iotcheck.plugin.ProcessWater212;
import com.iss.iotcheck.tools.DateHelper;
import com.iss.iotcheck.tools.StringUtil;

/**
 * 检验出的异常：
 * 1.212协议校验异常
 * 2.包含空格错误
 * 3.设备号不合规范 > 字母加数字
 * 4.数据包头错误
 * @author Liu Siyuan
 *
 */
public class IOTCheck {
    /**检测成功*/
    public static final String RE_CODE_OK = "0";
    /**CRC校验算法错误*/
    public static final String RE_CRC_ERR = "1";
    /**数据错误（不允许包含空格）*/
    public static final String RE_EMPTY_CHAR_ERR = "20";
    /**数据错误（其他）*/
    public static final String RE_212_OTHER_ERR = "21";
    /**设备号错误（字母加数字构成或者数字长度超过10位）*/
    public static final String RE_MN_ERR = "30";
    /**协议头部描述错误(包头：固定为 ##+数据段长度)*/
    public static final String RE_HEADER_ERR = "40";
    
    public static String checkMessage(String mess, int type) {
        if(Integer.valueOf(IProcessing.AIR_MONITOR_MONITOR_212) == type) {
            // 大气
            IProcessing p1 = new Process212();
            IProcessing p2 = new NationalStandard212();
            List<OlMonitorMinData> minData = null;
            if(p1.CheckData(mess)) {
                minData = (List<OlMonitorMinData>) p1.Process(mess);
            } else if(p2.CheckData(mess)) {
                minData = (List<OlMonitorMinData>) p2.Process(mess);
            } else {
                return RE_HEADER_ERR;
            }
            if(minData.size() == 0) {
                if(!checkCRC(mess)) {
                    // CRC校验错误
                    return RE_CRC_ERR;
                } else {                    
                    // 不知道什么情况
                    return RE_212_OTHER_ERR;
                }
            }
            OlMonitorMinData monitorMinData = minData.get(0);
            String deviceId = monitorMinData.getDeviceId();
            // 检查设备号
            if(!checkDevieId(deviceId)) {
                return RE_212_OTHER_ERR;
            }
            // 结果
            return "<html><body><b>校验成功</b><br/>设备：" + deviceId + "<br/>时间：" + DateHelper.format(monitorMinData.getMonitorTime())
            + "<br/>PM2.5: " + monitorMinData.getPm25() + "<br/>PM10: " + monitorMinData.getPm10()
            + "<br/>SO2: " + monitorMinData.getSo2() + "<br/>CO: " + monitorMinData.getCo()
            + "<br/>NO: " + monitorMinData.getNo() + "<br/>O3: " + monitorMinData.getO3()
            + "<br/>NO: " + monitorMinData.getNo() + "<br/>O3: " + monitorMinData.getO3()
            + "<br/>WS: " + monitorMinData.getWs() + "<br/>WD: " + monitorMinData.getWd()
            + "<br/>TEM: " + monitorMinData.getTem() + "<br/>RH: " + monitorMinData.getRh()
            + "<br/>PA: " + monitorMinData.getWs() + "<br/>TSP: " + monitorMinData.getTsp()
            + "</body>";
            
        } else if(Integer.valueOf(IProcessing.SURFACE_WATER_MONITOR_212) == type) {
            // 水
            IProcessing p = new ProcessWater212();
            List<OlMonitorWaterData> minData = null;
            if(p.CheckData(mess)) {
                if(p.CheckData(mess)) {
                    minData = (List<OlMonitorWaterData>) p.Process(mess);
                } else {
                    return RE_HEADER_ERR;
                }
            } else {
                return RE_HEADER_ERR;
            }
            if(minData.size() == 0) {
                if(!checkCRC(mess)) {
                    // CRC校验错误
                    return RE_CRC_ERR;
                } else {                    
                    // 不知道什么情况
                    return RE_212_OTHER_ERR;
                }
            }
            OlMonitorWaterData monitorMinData = minData.get(0);
            String deviceId = monitorMinData.getDeviceId();
            // 检查设备号
            if(!checkDevieId(deviceId)) {
                return RE_212_OTHER_ERR;
            }
            
            // 结果
            return "<html><body><b>校验成功</b><br/>设备：" + deviceId + "<br/>时间：" + DateHelper.format(monitorMinData.getMonitorTime())
            + "<br/>COD: " + monitorMinData.getCod() + "<br/>Bod: " + monitorMinData.getBod()
            + "<br/>NH3NH4: " + monitorMinData.getNH3NH4() + "<br/>KMNO4: " + monitorMinData.getKMnO4()
            + "<br/>PH: " + monitorMinData.getPH() + "<br/>DO: " + monitorMinData.getDO()
            + "<br/>Conductivity: " + monitorMinData.getConductivity() + "<br/>FTU: " + monitorMinData.getFTU()
            + "<br/>Temperature: " + monitorMinData.getTemperature() + "<br/>TP: " + monitorMinData.getTP()
            + "<br/>TN: " + monitorMinData.getTN() + "<br/>Cu: " + monitorMinData.getCu()
            + "<br/>Zn: " + monitorMinData.getZn() + "<br/>F: " + monitorMinData.getF()
            + "<br/>As: " + monitorMinData.getAs() + "<br/>Hg: " + monitorMinData.getHg()
            + "<br/>Cd: " + monitorMinData.getCd() + "<br/>Cr6: " + monitorMinData.getCr6()
            + "<br/>Pb: " + monitorMinData.getPb() + "<br/>Fe: " + monitorMinData.getFe()
            + "<br/>CN: " + monitorMinData.getCN() + "<br/>ArOH: " + monitorMinData.getArOH()
            + "<br/>Oil: " + monitorMinData.getOil() + "<br/>anionics: " + monitorMinData.getAnionics()
            + "<br/>sulfide: " + monitorMinData.getSulfide() + "<br/>NO3N: " + monitorMinData.getNO3N()
            + "<br/>biotoxicity: " + monitorMinData.getBiotoxicity() + "<br/>chlorophyl_a: " + monitorMinData.getChlorophyla()
            + "<br/>algae: " + monitorMinData.getAlgae()
            + "</body>";
        }
        return RE_CODE_OK;
    }

    private static boolean checkCRC(String mess) {
        int start = mess.indexOf("ST=");
        int end = mess.lastIndexOf("&&");
        String target = mess.substring(start, end + 2);
        String CRC = Integer.toHexString(StringUtil.getCRC(target)).toUpperCase();
        return CRC.equals(mess.substring(end + 2, mess.length()));
    }

    public static void main(String[] args) {
//        String[] dev = new String[] {
//                "123",
//                "abc",
//                "123abc",
//                "123456"
//        };
//        Arrays.asList(dev).forEach(s -> System.out.println(checkDevieId(s)));
        System.out.println(checkCRC("##0223ST=22;CN=2011;PW=123456;MN=C037800AM2011;CP=&&DataTime=20180328235000;SO2-Rtd=19.0,SO2-Flag=N;NO2-Rtd=25.0,NO2-Flag=N;CO-Rtd=0.687,CO-Flag=N;O3-Rtd=76.0,O3-Flag=N;PM25-Rtd=75.0,PM25-Flag=N;PM10-Rtd=374.0,PM10-Flag=N&&4380"));
    }
    
    private static boolean checkDevieId(String str) {
        if(str == null || str.length() ==0 || str.length() < 10) {
            return false;
        }
        return containNum(str) && containNum(str);
    }
    
    public static boolean containNum(String str){
        char ch[] = str.toCharArray();
        boolean contain=false;
        for(int i=0;i<ch.length;i++){
            if(Character.isDigit(ch[i])){
                contain=true;
            }
        }
        return contain;
    }

    public static boolean containLetter(String str){
        char ch[] = str.toCharArray();
        boolean contain=false;
        for(int i=0;i<ch.length;i++){
            if(Character.isLetter(ch[i])){
                contain=true;
            }
        }
        return contain;
    }
}
