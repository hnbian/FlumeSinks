package org.apache.flume.sink.hbase2.builder.rowkey;

public class Bulider {
    /**
     * 组合byte 数组
     * @param b1
     * @param b2
     * @return
     * @throws Exception
     */
    static byte[] byteUnion(byte[] b1, byte[] b2) {
        byte[] b3 = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        return b3;
    }
}
