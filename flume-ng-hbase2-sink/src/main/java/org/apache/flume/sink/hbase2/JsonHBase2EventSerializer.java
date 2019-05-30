package org.apache.flume.sink.hbase2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.hbase2.builder.BuliderFactory;
import org.apache.flume.sink.hbase2.builder.PutBulider;
import org.apache.flume.sink.hbase2.builder.RowkeyBulider;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 解析JSON 格式的数据，将json的kv与和base的列名与列值相对应
 */
public class JsonHBase2EventSerializer implements HBase2EventSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonHBase2EventSerializer.class);
    private String rowPrefix;
    private byte[] incrementRow;
    private byte[] cf;
    private byte[] cfHeader = "header".getBytes();
    private byte[] plCol;
    private byte[] incCol;
    private KeyType keyType;
    private byte[] body;
    private byte[] event;
    private boolean enableWal = true;
    private Map<String, String> headers;

    //private String rowkeyFormat ;
    //前缀后缀属性
    //private String fixType ;
    // rowkey 拼接字段
    private String columns;
    private RowkeyBulider rowkeyBulider;
    private PutBulider putBulider;
    /**
     * 是否将header保存到Hbase中。 默认false
     */
    private boolean saveHeader = false;


    public JsonHBase2EventSerializer() {
    }

    @Override
    public void configure(Context context) {
        incrementRow = context.getString("incrementRow", "incRow").getBytes(Charsets.UTF_8);
        saveHeader = context.getBoolean(HBase2SinkConfigurationConstants.SAVE_HEADER, saveHeader);


        logger.info("JsonHBase2EventSerializer.context.toString()=>" + context.toString());

        String rowkeyFix = context.getString(HBase2SinkConfigurationConstants.ROWKEY_FIX);
        String rowkeyFixType = context.getString(HBase2SinkConfigurationConstants.ROWKEY_FIX_TYPE);
        String rowkeyFixColumnFormat = context.getString(HBase2SinkConfigurationConstants.ROWKEY_FIX_COLUMN_FORMAT);
        String rowkeyFixColumnName = context.getString(HBase2SinkConfigurationConstants.ROWKEY_FIX_COLUMN_NAME);
        String rowkeyColumns = context.getString(HBase2SinkConfigurationConstants.ROWKEY_COLUMNS);
        String rowkeySplit = context.getString(HBase2SinkConfigurationConstants.ROWKEY_SPLIT,"-");
        String columnFamily = context.getString(HBase2SinkConfigurationConstants.CONFIG_COLUMN_FAMILY);




        rowkeyBulider = BuliderFactory.getRowkeyBulider(
                rowkeyFix,
                rowkeyFixType,
                rowkeyFixColumnFormat,
                rowkeyFixColumnName,
                rowkeyColumns,
                rowkeySplit
        );

        logger.info("rowkeyBulider.getClass()=> "+rowkeyBulider.getClass());

        putBulider = BuliderFactory.getPutBulider(saveHeader, rowkeyBulider, columnFamily.getBytes());

        logger.info("putBulider.getClass()=>" + putBulider.getClass());

        /*String rowkeyFormat = context.getString("rowkeyFormat","uuid");
        rowkeyBulider = RowkeyBuliderFactory.getRowkeyBulider(rowkeyFormat);*/

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
        return null;
    }

    @Override
    public List<Mutation> getMutations() throws FlumeException {
        List<Row> actions = new LinkedList<>();
        List<Mutation> mutations = new ArrayList<>();

        try {
            JSONObject jb = JSON.parseObject(new String(body));
            Put put = putBulider.bulidPut(jb, headers);
            //put.setDurability(enableWal ? Durability.USE_DEFAULT : Durability.SKIP_WAL);
            //put.setDurability(Durability.ASYNC_WAL); //最快但是会重复写入或丢数据
            //put.setDurability(Durability.USE_DEFAULT); //800+
            put.setDurability(Durability.SKIP_WAL); //1600+
            mutations.add(put);
        } catch (Exception e) {
            throw new FlumeException("Could not get row key!", e);
        }

        return mutations;
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
