package com.hch.chat_simple.util;


/**
 * Snowflake 分布式唯一ID生成器
 * 
 * 结构：
 *   0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 
 *   1位符号位 | 41位时间戳 | 10位机器ID | 12位序列号
 */
public class SnowflakeIdGen {


    // ============================== Fields ===================================
    // 起始时间戳（2023-01-01）
    private final static long START_TIMESTAMP = 1672531200000L;

    // 各部分的位数
    // private final static long DATA_CENTER_ID_BITS = 5L;
    private final static long WORKER_ID_BITS = 10L;
    private final static long SEQUENCE_BITS = 12L;

    // 最大值
    // private final static long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 各部分的左移位
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    // private final static long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // 参数
    // private final long dataCenterId;  // 数据中心ID
    private final long workerId;      // 机器ID
    private long sequence = 0L;       // 序列号
    private long lastTimestamp = -1L; // 上次生成ID的时间戳

    // ============================== Constructors =============================
    
    public SnowflakeIdGen(long workerId) {
        
        // if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
        //     throw new IllegalArgumentException("Data center ID can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0");
        // }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }
        // this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    // ============================== Methods ==================================
    /**
     * 生成下一个ID（线程安全）
     */
    public synchronized long nextId() {
        long currentTimestamp = timeGen();

        // 如果当前时间小于上次生成ID的时间，说明系统时钟回退，抛出异常
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        // 如果是同一时间生成的，则进行序列号自增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出（超过最大值）
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                currentTimestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，序列号重置
            sequence = 0L;
        }

        // 更新上次生成ID的时间戳
        lastTimestamp = currentTimestamp;

        // 拼接各部分数据
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                // | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回当前时间（毫秒）
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    // ============================== Usage ====================================
    public static void main(String[] args) {
        // 示例：创建ID生成器（数据中心ID=1，机器ID=1）
        SnowflakeIdGen idGenerator = new SnowflakeIdGen(1);
        
        // 生成10个ID
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.nextId();
            System.out.println("生成的ID: " + id + " ，长度：" + String.valueOf(id).length());
        }
    }
}
