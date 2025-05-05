package org.sprouts.tss.hardware.pojo;

import lombok.Data;

/**
 * @author wangmin
 */
@Data
public class ProcessInfo {
    private String pid;
    private String name;
    private String usedMemory;

    @Override
    public String toString() {
        return "pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", usedMemory='" + usedMemory + '\'';
    }
}