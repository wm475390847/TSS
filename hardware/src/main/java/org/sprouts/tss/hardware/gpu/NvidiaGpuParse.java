package org.sprouts.tss.hardware.gpu;

import org.sprouts.tss.hardware.pojo.GpuInfo;
import org.sprouts.tss.hardware.pojo.ProcessInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2022/4/14 09:15
 */
@Slf4j
public class NvidiaGpuParse extends BaseGpuParse {

    private static final String REG = "<!DOCTYPE.*.dtd\">";

    /**
     * 获取gpu信息（暂时只支持nvidia-smi）
     *
     * @return gpu信息集合
     * @throws DocumentException xml解析错误
     */
    private List<GpuInfo> convertXmlToGpuObject(String xmlGpu) throws DocumentException {
        //忽略dtd
        xmlGpu = xmlGpu.replaceAll(REG, "");
        Document document = DocumentHelper.parseText(xmlGpu);
        List<Element> gpu = document.getRootElement().elements("gpu");
        List<GpuInfo> gpuInfoList = new ArrayList<>();
        gpu.forEach(element -> {
            GpuInfo gpuInfo = new GpuInfo();

            //显卡内存
            Element fbMemoryUsage = element.element("fb_memory_usage");
            String total = fbMemoryUsage.element("total").getText();
            String used = fbMemoryUsage.element("used").getText();
            String free = fbMemoryUsage.element("free").getText();
            gpuInfo.setTotalMemory(total);
            gpuInfo.setUsedMemory(used);
            gpuInfo.setFreeMemory(free);

            //gpu名称
            String uuid = element.element("uuid").getText();
            gpuInfo.setName(uuid);

            //显卡gpu的使用率
            Element utilization = element.element("utilization");
            Element gpuUtil = utilization.element("gpu_util");
            int intGpuRatio = Integer.parseInt(gpuUtil.getText().split(" ")[0]);
            gpuInfo.setGpuRatio(intGpuRatio);

            //进程
            Element processes = element.element("processes");
            List<Element> infos = processes.elements("process_info");
            List<ProcessInfo> processInfos = new ArrayList<>();
            infos.forEach(info -> {
                ProcessInfo processInfo = new ProcessInfo();
                String pid = info.element("pid").getText();
                String name = info.element("process_name").getText();
                String usedMemory = info.element("used_memory").getText();
                processInfo.setPid(pid);
                processInfo.setName(name);
                processInfo.setUsedMemory(usedMemory);
                processInfos.add(processInfo);
            });
            gpuInfo.setProcessInfos(processInfos);
            int intTotal = Integer.parseInt(total.split(" ")[0]);
            int intUsed = Integer.parseInt(used.split(" ")[0]);
            gpuInfo.setMemoryRatio((int) ((float) intUsed / intTotal * 100));
            gpuInfoList.add(gpuInfo);
        });
        return gpuInfoList;
    }

    /**
     * 通过命令xml格式显卡信息
     *
     * @return xml字符串
     * @throws IOException 获取显卡信息错误
     */
    private String getGpuXmlInfo(String command) throws IOException {
        String result;
        Process process = Runtime.getRuntime().exec(command);
        try (InputStream inputStream = process.getInputStream()) {
            result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        if (process.isAlive()) {
            process.destroy();
        }
        return result;
    }

    @Override
    protected Optional<List<GpuInfo>> find(String command) {
        try {
            String gpuXmlInfo = getGpuXmlInfo(command);
            List<GpuInfo> gpuInfos = convertXmlToGpuObject(gpuXmlInfo);
            return Optional.of(gpuInfos);
        } catch (Exception e) {
            log.error("获取gpu信息error , message : {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
