package org.sprouts.tss.hardware;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;

import com.alibaba.fastjson.JSONObject;
import org.sprouts.tss.hardware.enums.GpuTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.sprouts.tss.hardware.pojo.*;
import org.sprouts.tss.robot.MarkdownRobot;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangmin
 * @date 2022/4/11 15:40
 */
@Slf4j
public class Monitoring {

    private static final Properties PROPERTIES = Property.getInstance().parse();

    public static void main(String[] args) {
        run();
        //主线程不关闭
        ThreadUtil.waitForDie();
    }

    private static void run() {
        long time = Long.parseLong(PROPERTIES.getProperty("thread_sleep"));
        String school = PROPERTIES.getProperty("school");

        AtomicLong threadSleep = new AtomicLong(time);
        Circle.newCircle(() -> {
            SystemInfo systemInfo = new SystemInfo();

            //获取cpu利用率
            CPUInfo cpuInfo = getOsInfo();
            //获取内存数据
            MemoryInfo memoryInfo = getMemoryInfo();
            //获取硬盘使用量
            DiskInfo diskInfo = getDiskInfo();

            //获取gpu信息
            GpuTypeEnum gpuTypeEnum = GpuTypeEnum.findByGpuType(PROPERTIES.getProperty("gpu_type"));
            Optional<List<GpuInfo>> gpuInfos = gpuTypeEnum == null ? Optional.empty() :
                    gpuTypeEnum.getGpuParse().getGpuInfo();

            systemInfo.setCpuInfo(cpuInfo);
            systemInfo.setMemoryInfo(memoryInfo);
            systemInfo.setGpuInfo(gpuInfos);
            systemInfo.setDiskInfo(diskInfo);

            JSONObject object = (JSONObject) JSONObject.toJSON(systemInfo);

            //打印信息
            log.info("{}", object);

            String message = " #### **服务器性能监控：**" + "\n\n"
                    + " #### "
                    + school + new Date() + "\n\n"
                    + " ###### "
                    + cpuInfo + "\n\n"
                    + " ###### "
                    + memoryInfo + "\n\n"
                    + " ###### "
                    + diskInfo + "\n\n"
                    + " ###### "
                    + gpuInfos;

            double cpuThresholdValue = Double.parseDouble(PROPERTIES.getProperty("cpu_threshold_value"));
            double memoryThresholdValue = Double.parseDouble(PROPERTIES.getProperty("memory_threshold_value"));
            int gpuThresholdValue = Integer.parseInt(PROPERTIES.getProperty("gpu_threshold_value"));
            int gpuMemoryThresholdValue = Integer.parseInt(PROPERTIES.getProperty("gpu_memory_threshold_value"));

            if (cpuInfo.getCpuRatio() > cpuThresholdValue
                    || memoryInfo.getMemoryRatio() > memoryThresholdValue
                    || !check(gpuInfos, gpuThresholdValue, gpuMemoryThresholdValue)) {
                sendDingDing(message);
            }
            System.out.println();
        }, school + "监控", threadSleep.get());
    }

    /**
     * 校验gpu数据
     *
     * @param gpuInfos           gpu信息
     * @param gpuThreshold       gpu使用率
     * @param gpuMemoryThreshold 显存使用率
     * @return boolean
     */
    private static boolean check(Optional<List<GpuInfo>> gpuInfos, int gpuThreshold, int gpuMemoryThreshold) {
        //不存在gpu信息就不进行判断，直接返回true
        if (!gpuInfos.isPresent()) {
            return true;
        }
        List<GpuInfo> list = gpuInfos.get();
        for (GpuInfo gpuInfo : list) {
            if (gpuInfo.getGpuRatio() > gpuThreshold || gpuInfo.getMemoryRatio() > gpuMemoryThreshold) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取cpu利用率
     */
    private static CPUInfo getOsInfo() {
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();
        double free = cpuInfo.getFree();
        DecimalFormat format = new DecimalFormat("#.00");
        double cpuRatio = Double.parseDouble(format.format(100.0D - free));
        CPUInfo cpu = new CPUInfo();
        cpu.setCpuRatio(cpuRatio);
        return cpu;
    }

    /**
     * 获取内存数据
     */
    private static MemoryInfo getMemoryInfo() {
        long memoryTotal = OshiUtil.getMemory().getTotal() / 1024 / 1024;
        //可用内存
        long memoryAvailable = OshiUtil.getMemory().getAvailable() / 1024 / 1024;
        long memoryUse = memoryTotal - memoryAvailable;

        DecimalFormat format = new DecimalFormat("#.00");

        //四舍五入取小数点后4位
        int cardinal = (int) Math.pow(10, 4);
        double decimal = (double) Math.round((double) memoryUse / memoryTotal * cardinal) / cardinal * 100;
        double memoryRatio = Double.parseDouble(format.format(decimal));
        MemoryInfo memoryInfo = new MemoryInfo();
        memoryInfo.setMemoryUse(memoryUse);
        memoryInfo.setMemoryTotal(memoryTotal);
        memoryInfo.setMemoryRatio(memoryRatio);
        return memoryInfo;
    }

    /**
     * 获取硬盘使用量
     */
    private static DiskInfo getDiskInfo() {
        File win = new File("/");
        DiskInfo diskInfo = new DiskInfo();
        if (win.exists()) {
            long total = win.getTotalSpace();
            long freeSpace = win.getFreeSpace();
            long diskTotal = total / 1024 / 1024 / 1024;
            long diskResidue = freeSpace / 1024 / 1024 / 1024;
            long diskUse = (total - freeSpace) / 1024 / 1024 / 1024;

            DecimalFormat format = new DecimalFormat("#.00");
            int cardinal = (int) Math.pow(10, 4);
            double decimal = (double) Math.round((double) diskUse / diskTotal * cardinal) / cardinal * 100;
            double diskRatio = Double.parseDouble(format.format(decimal));

            diskInfo.setDiskUse(diskUse);
            diskInfo.setDiskResidue(diskResidue);
            diskInfo.setDiskTotal(diskTotal);
            diskInfo.setDiskRatio(diskRatio);
        }
        return diskInfo;
    }

    private static void sendDingDing(String message) {
        String webhook = PROPERTIES.getProperty("webhook");
        String keyword = PROPERTIES.getProperty("keyword");
        new MarkdownRobot.Builder().text(message).isAtAll(false).webhook(webhook).keyword(keyword).build().send();
    }
}
