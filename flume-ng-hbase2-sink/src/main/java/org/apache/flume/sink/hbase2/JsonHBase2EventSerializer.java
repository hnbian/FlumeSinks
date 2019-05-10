package org.apache.flume.sink.hbase2;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 解析JSON 格式的数据，将json的kv与和base的列名与列值相对应
 */
public class JsonHBase2EventSerializer  implements HBase2EventSerializer  {
    private String rowPrefix;
    private byte[] incrementRow;
    private byte[] cf;
    private byte[] cfHeader = "header".getBytes();
    private byte[] plCol;
    private byte[] incCol;
    private KeyType keyType;
    private byte[] body;
    private byte[] event;
    private Map<String, String> headers;
    private String rowkeyFormat ;
    //前缀后缀属性
    private String fixType ;
    // rowkey 拼接字段
    private String columns;



    public JsonHBase2EventSerializer() {
    }

    @Override
    public void configure(Context context) {
        //rowPrefix = context.getString("rowPrefix", "default");
        incrementRow =
                context.getString("incrementRow", "incRow").getBytes(Charsets.UTF_8);
        //String suffix = context.getString("suffix", "uuid");

        /*String bodyColumn = context.getString("bodyColumn", "pCol");
        String incColumn = context.getString("incrementColumn", "iCol");*/
        rowkeyFormat = context.getString("rowkeyFormat","uuid");
        String [] rkColumns = null;
        if(!"uuid".equals(rowkeyFormat)){
            rkColumns = rowkeyFormat.split("\\|");
        }


        if ( null != rkColumns && rkColumns.length == 2) {
            columns = rkColumns[1];
            fixType = rkColumns[0].split(",")[0];
            switch (rkColumns[0].split(",")[1]) {
                case "timestamp":
                    keyType = KeyType.TS;
                    break;
                case "random":
                    keyType = KeyType.RANDOM;
                    break;
                case "nano":
                    keyType = KeyType.TSNANO;
                    break;
                default:
                    keyType = KeyType.UUID;
                    break;
            }
        }else if(null != rkColumns && rkColumns.length == 1){
            columns = rkColumns[0];
        }
    }

    @Override
    public void configure(ComponentConfiguration conf) {
    }

    @Override
    public void initialize(Event event, byte[] cf) {
        this.body = event.getBody();
        this.cf = cf;
        this.headers = event.getHeaders();
    }

    @Override
    public List<Row> getActions() throws FlumeException {
        List<Row> actions = new LinkedList<>();
//        if (plCol != null) {

            try {
               /* if (keyType == KeyType.TS) {
                    rowKey = SimpleRowKeyGenerator.getTimestampKey(rowPrefix);
                } else if (keyType == KeyType.RANDOM) {
                    rowKey = SimpleRowKeyGenerator.getRandomKey(rowPrefix);
                } else if (keyType == KeyType.TSNANO) {
                    rowKey = SimpleRowKeyGenerator.getNanoTimestampKey(rowPrefix);
                } else {
                    rowKey = SimpleRowKeyGenerator.getUUIDKey(rowPrefix);
                }*/
                byte[] rowKey;
                System.out.println("getActions rowkey==>"+rowkeyFormat);
                JSONObject jb = JSON.parseObject(new String(body));

                if(null != fixType){
                    //通过前缀或后缀加上列名组合
                    rowKey = JsonRowKeyGenerator.getRowKey(jb,columns,fixType,keyType);
                }else if(null != columns){
                    rowKey = JsonRowKeyGenerator.getRowKey(jb,columns);
                }else{
                    rowKey = JsonRowKeyGenerator.getUUIDKey("default");
                }

                Put put = new Put(rowKey);
                for (String key : jb.keySet()) {
                    put.addColumn(cf, Bytes.toBytes(key), Bytes.toBytes(jb.getString(key)));
                }
                for(String key:headers.keySet()){
                    put.addColumn(cfHeader, Bytes.toBytes(key), Bytes.toBytes(headers.get(key)));
                }
                actions.add(put);
            } catch (Exception e) {
                throw new FlumeException("Could not get row key!", e);
            }

       // }
        return actions;
    }

    @Override
    public List<Increment> getIncrements() {
        List<Increment> incs = new LinkedList<>();
        if (incCol != null) {
            Increment inc = new Increment(incrementRow);
            inc.addColumn(cf, incCol, 1);
            incs.add(inc);
        }
        return incs;
    }

    @Override
    public void close() {
    }

    public enum KeyType {
        UUID,
        RANDOM,
        TS,
        TSNANO
    }

}
