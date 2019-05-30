package org.apache.flume.sink.hbase2.builder.put;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.sink.hbase2.builder.PutBulider;
import org.apache.flume.sink.hbase2.builder.RowkeyBulider;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Map;

public class PutWithHeaderBulider  implements PutBulider {
    private RowkeyBulider rowkeyBulider;
    private byte[] cf;
    private byte[] cfHeader = "header".getBytes();

    public PutWithHeaderBulider(RowkeyBulider rowkeyBulider,byte[] cf) {
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

            for(String key:headers.keySet()){
                put.addColumn(cfHeader, Bytes.toBytes(key), Bytes.toBytes(headers.get(key)));
            }

            return put;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
