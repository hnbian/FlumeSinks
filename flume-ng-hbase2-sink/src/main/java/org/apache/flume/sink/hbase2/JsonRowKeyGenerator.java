package org.apache.flume.sink.hbase2;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;
import org.apache.flume.sink.hbase2.JsonHBase2EventSerializer.KeyType;

/**
 * 创建RowKey 工具类
 */
public class JsonRowKeyGenerator {


    public static byte[] getRowKey(JSONObject jb, String columns, String fixType, KeyType keyTpye) throws UnsupportedEncodingException,CloumnNotFoundException {

        byte[] columnString = getRowKey(jb, columns);
        byte[] fix = getfix(keyTpye);
        //加前缀
        switch (fixType) {
            case "prefix":
                return byteUnion(fix, columnString);
            case "suffix":
                return byteUnion(columnString, fix);
            default:
                return JsonRowKeyGenerator.getUUIDKey("");
        }
    }

    /**
     * 获取指定前缀或后缀格式
     */
    private static byte[] getfix(KeyType keyTpye) throws UnsupportedEncodingException {
        if (keyTpye == KeyType.TS) {
            return JsonRowKeyGenerator.getTimestampKey("");
        } else if (keyTpye == KeyType.RANDOM) {
            return JsonRowKeyGenerator.getRandomKey("");
        } else if (keyTpye == KeyType.TSNANO) {
            return JsonRowKeyGenerator.getNanoTimestampKey("");
        } else {
            return JsonRowKeyGenerator.getUUIDKey("");
        }
    }

    /**
     * 组合byte 数组
     * @param b1
     * @param b2
     * @return
     * @throws Exception
     */
    private static byte[] byteUnion(byte[] b1, byte[] b2) {
        byte[] b3 = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, b3, 0, b1.length);
        System.arraycopy(b2, 0, b3, b1.length, b2.length);
        return b3;
    }

    /**
     * 通过 body中的value值拼接rowkey
     *
     * @param jb
     * @param columns
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] getRowKey(JSONObject jb, String columns) throws UnsupportedEncodingException,CloumnNotFoundException {

        StringBuffer sb = new StringBuffer();
        for (String k : columns.split(",")) {
            if(null == jb.get(k)){
                throw new CloumnNotFoundException("404","The Cloumn "+ k + " Not Found ");
            }
            sb.append(jb.get(k));
        }
        return sb.toString().getBytes("UTF8");
    }


    public static byte[] getRowKey(String rk) throws UnsupportedEncodingException {
        return getUUIDKey("default");
    }

    public static byte[] getUUIDKey(String prefix) throws UnsupportedEncodingException {
        return (prefix + UUID.randomUUID().toString()).getBytes("UTF8");
    }

    public static byte[] getRandomKey(String prefix) throws UnsupportedEncodingException {
        return (prefix + String.valueOf(new Random().nextLong())).getBytes("UTF8");
    }

    public static byte[] getTimestampKey(String prefix) throws UnsupportedEncodingException {
        return (prefix + String.valueOf(System.currentTimeMillis())).getBytes("UTF8");
    }

    public static byte[] getNanoTimestampKey(String prefix) throws UnsupportedEncodingException {
        return (prefix + String.valueOf(System.nanoTime())).getBytes("UTF8");
    }

}
