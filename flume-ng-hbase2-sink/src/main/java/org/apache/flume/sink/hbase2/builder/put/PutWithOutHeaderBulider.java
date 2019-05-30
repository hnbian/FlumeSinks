package org.apache.flume.sink.hbase2.builder.put;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.sink.hbase2.builder.PutBulider;
import org.apache.flume.sink.hbase2.builder.RowkeyBulider;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PutWithOutHeaderBulider implements PutBulider {
    private static final Logger logger = LoggerFactory.getLogger(PutWithOutHeaderBulider.class);
    private RowkeyBulider rowkeyBulider;
    private byte[] cf;

    public PutWithOutHeaderBulider(RowkeyBulider rowkeyBulider, byte[] cf) {
        this.rowkeyBulider = rowkeyBulider;
        this.cf = cf;
    }

    @Override
    public Put bulidPut(JSONObject jsonObject, Map<String, String> headers) {
        try {

            byte[] rowKey = rowkeyBulider.bulider(jsonObject);
            Put put = new Put(rowKey);
            for (String key : jsonObject.keySet()) {
                put.addColumn(cf, Bytes.toBytes(key), Bytes.toBytes(jsonObject.getString(key)));
            }
            return put;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
