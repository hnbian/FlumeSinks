package org.apache.flume.sink.hbase2.builder.rowkey;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.sink.hbase2.CloumnNotFoundException;
import org.apache.flume.sink.hbase2.builder.RowkeyBulider;
import org.apache.flume.sink.hbase2.builder.RowkeyFixBulider;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.UnsupportedEncodingException;

public class SuffixColumnTimeRowkeyBulider extends Bulider implements RowkeyBulider {
    private RowkeyFixBulider rowkeyFixBulider;
    private ColumnRowkeyBulider columnRowkeyBulider;
    private String columnTimekey;
    private byte[] split;


    public SuffixColumnTimeRowkeyBulider(RowkeyFixBulider rowkeyFixBulider,String columns,String columnTime,String rowkeySplit){
        this.rowkeyFixBulider = rowkeyFixBulider;
        this.columnRowkeyBulider = new ColumnRowkeyBulider(columns,rowkeySplit);
        this.columnTimekey = columnTime;
        this.split = rowkeySplit.getBytes();
    }

    @Override
    public byte[] bulider(JSONObject jsonObject) throws UnsupportedEncodingException, CloumnNotFoundException {
        String columnTimeValue = jsonObject.getString(columnTimekey);
        if (null == columnTimeValue) {
            throw new CloumnNotFoundException("404", "The Cloumn " + columnTimekey + " Not Found ");
        }
        return Bytes.add(columnRowkeyBulider.bulider(jsonObject),split,rowkeyFixBulider.bulider(columnTimeValue));
    }
}
