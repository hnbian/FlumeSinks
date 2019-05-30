package org.apache.flume.sink.hbase2.builder.rowkey;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.sink.hbase2.CloumnNotFoundException;
import org.apache.flume.sink.hbase2.builder.RowkeyBulider;
import org.apache.flume.sink.hbase2.builder.RowkeyFixBulider;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.UnsupportedEncodingException;

/**
 * 前缀加列值作为rowkey
 */
public class PrefixColumnRowkeyBulider extends Bulider implements RowkeyBulider {
    private RowkeyFixBulider rowkeyFixBulider;
    private ColumnRowkeyBulider columnRowkeyBulider;
    private byte[] split;


    public PrefixColumnRowkeyBulider(RowkeyFixBulider rowkeyFixBulider,String columns,String rowkeySplit){
        this.rowkeyFixBulider = rowkeyFixBulider;
        this.columnRowkeyBulider = new ColumnRowkeyBulider(columns,rowkeySplit);
        this.split = rowkeySplit.getBytes();
    }


    @Override
    public byte[] bulider(JSONObject jsonObject) throws UnsupportedEncodingException, CloumnNotFoundException {
        return Bytes.add(rowkeyFixBulider.bulider(),split,columnRowkeyBulider.bulider(jsonObject));
    }
}
