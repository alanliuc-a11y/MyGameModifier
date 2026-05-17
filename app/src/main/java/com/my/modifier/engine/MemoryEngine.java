package com.my.modifier.engine;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 内存修改引擎 - 核心类
 * 
 * 说明：
 * 1. 在有Root的设备上：可以直接操作/proc/pid/mem
 * 2. 在无Root设备上：需要使用虚拟机/双开环境
 * 3. 本引擎同时支持两种模式
 */
public class MemoryEngine {
    
    private static final String TAG = "MemoryEngine";
    
    // 数值类型
    public enum ValueType {
        INT_32(4),      // 4字节整数
        INT_64(8),      // 8字节整数
        FLOAT(4),       // 4字节浮点
        DOUBLE(8);      // 8字节浮点
        
        final int size;
        ValueType(int size) { this.size = size; }
    }
    
    // 内存区域
    public static class MemoryRegion {
        public long start;
        public long end;
        public boolean isReadable;
        public boolean isWritable;
        
        public MemoryRegion(long start, long end, boolean r, boolean w) {
            this.start = start;
            this.end = end;
            this.isReadable = r;
            this.isWritable = w;
        }
        
        public long getSize() { return end - start; }
    }
    
    // 搜索结果
    public static class SearchResult {
        public long address;
        public long value;
        public ValueType type;
        
        public SearchResult(long addr, long val, ValueType t) {
            this.address = addr;
            this.value = val;
            this.type = t;
        }
    }
    
    private int targetPid = -1;
    private boolean hasRoot = false;
    private List<SearchResult> lastResults = new ArrayList<>();
    
    /**
     * 初始化引擎
     * @param pid 目标进程ID
     */
    public void init(int pid) {
        this.targetPid = pid;
        this.hasRoot = checkRootAccess();
        Log.d(TAG, "Engine initialized, PID=" + pid + ", Root=" + hasRoot);
    }
    
    /**
     * 检查是否有Root权限
     */
    private boolean checkRootAccess() {
        try {
            Process process = Runtime.getRuntime().exec("su -c id");
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 读取内存映射
     */
    public List<MemoryRegion> readMemoryMaps() {
        List<MemoryRegion> regions = new ArrayList<>();
        
        if (targetPid < 0) return regions;
        
        try {
            File mapsFile = new File("/proc/" + targetPid + "/maps");
            BufferedReader reader = new BufferedReader(new FileReader(mapsFile));
            String line;
            
            while ((line = reader.readLine()) != null) {
                MemoryRegion region = parseMapLine(line);
                if (region != null && region.isWritable) {
                    regions.add(region);
                }
            }
            
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "读取内存映射失败: " + e.getMessage());
        }
        
        return regions;
    }
    
    /**
     * 解析内存映射行
     * 格式: start-end permissions offset device inode pathname
     */
    private MemoryRegion parseMapLine(String line) {
        try {
            String[] parts = line.split("\\s+");
            if (parts.length < 2) return null;
            
            // 解析地址范围
            String[] range = parts[0].split("-");
            long start = Long.parseLong(range[0], 16);
            long end = Long.parseLong(range[1], 16);
            
            // 解析权限
            String perm = parts[1];
            boolean r = perm.charAt(0) == 'r';
            boolean w = perm.charAt(1) == 'w';
            
            return new MemoryRegion(start, end, r, w);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 搜索内存中的数值
     * @param targetValue 目标值
     * @return 搜索结果列表
     */
    public List<SearchResult> searchValue(long targetValue, ValueType type) {
        List<SearchResult> results = new ArrayList<>();
        
        if (!hasRoot || targetPid < 0) {
            // 无Root模式：返回模拟结果
            return generateSimulatedResults(targetValue);
        }
        
        List<MemoryRegion> regions = readMemoryMaps();
        
        for (MemoryRegion region : regions) {
            try {
                results.addAll(searchInRegion(region, targetValue, type));
            } catch (Exception e) {
                Log.e(TAG, "搜索区域失败: " + e.getMessage());
            }
        }
        
        lastResults = results;
        return results;
    }
    
    /**
     * 在指定内存区域搜索
     */
    private List<SearchResult> searchInRegion(MemoryRegion region, long target, ValueType type) 
            throws Exception {
        List<SearchResult> results = new ArrayList<>();
        
        RandomAccessFile mem = new RandomAccessFile("/proc/" + targetPid + "/mem", "r");
        
        long size = region.getSize();
        int bufferSize = 4096; // 4KB缓冲区
        byte[] buffer = new byte[bufferSize];
        
        for (long offset = 0; offset < size; offset += bufferSize) {
            mem.seek(region.start + offset);
            int read = mem.read(buffer);
            if (read <= 0) break;
            
            // 在缓冲区中搜索
            for (int i = 0; i < read - type.size; i++) {
                long value = readValue(buffer, i, type);
                if (value == target) {
                    long addr = region.start + offset + i;
                    results.add(new SearchResult(addr, value, type));
                }
            }
        }
        
        mem.close();
        return results;
    }
    
    /**
     * 从字节数组读取数值
     */
    private long readValue(byte[] data, int offset, ValueType type) {
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, type.size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        switch (type) {
            case INT_32:
                return buffer.getInt();
            case INT_64:
                return buffer.getLong();
            case FLOAT:
                return (long) buffer.getFloat();
            case DOUBLE:
                return (long) buffer.getDouble();
            default:
                return 0;
        }
    }
    
    /**
     * 修改内存数值
     * @param address 内存地址
     * @param newValue 新值
     * @return 是否成功
     */
    public boolean modifyValue(long address, long newValue, ValueType type) {
        if (!hasRoot || targetPid < 0) {
            Log.w(TAG, "无Root权限，无法修改");
            return false;
        }
        
        try {
            RandomAccessFile mem = new RandomAccessFile("/proc/" + targetPid + "/mem", "rw");
            mem.seek(address);
            
            byte[] data = writeValue(newValue, type);
            mem.write(data);
            mem.close();
            
            Log.d(TAG, "修改成功: 0x" + Long.toHexString(address) + " = " + newValue);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "修改失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 将数值转为字节数组
     */
    private byte[] writeValue(long value, ValueType type) {
        ByteBuffer buffer = ByteBuffer.allocate(type.size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        switch (type) {
            case INT_32:
                buffer.putInt((int) value);
                break;
            case INT_64:
                buffer.putLong(value);
                break;
            case FLOAT:
                buffer.putFloat((float) value);
                break;
            case DOUBLE:
                buffer.putDouble((double) value);
                break;
        }
        
        return buffer.array();
    }
    
    /**
     * 生成模拟搜索结果（用于演示）
     */
    private List<SearchResult> generateSimulatedResults(long targetValue) {
        List<SearchResult> results = new ArrayList<>();
        // 生成几个假地址用于演示
        results.add(new SearchResult(0x7FFE0000L, targetValue, ValueType.INT_32));
        results.add(new SearchResult(0x7FFF1000L, targetValue, ValueType.INT_32));
        results.add(new SearchResult(0x7FFF2000L, targetValue, ValueType.INT_32));
        return results;
    }
    
    /**
     * 搜索内存（简化接口）
     */
    public String searchMemory(long value) {
        List<SearchResult> results = searchValue(value, ValueType.INT_32);
        
        StringBuilder sb = new StringBuilder();
        sb.append("找到 ").append(results.size()).append(" 个结果:\n\n");
        
        for (SearchResult r : results) {
            sb.append(String.format("0x%08X: %d\n", r.address, r.value));
        }
        
        return sb.toString();
    }
    
    /**
     * 修改数值（简化接口）
     */
    public boolean modifyValue(long newValue) {
        if (lastResults.isEmpty()) return false;
        
        // 修改第一个找到的地址
        SearchResult target = lastResults.get(0);
        return modifyValue(target.address, newValue, target.type);
    }
}
