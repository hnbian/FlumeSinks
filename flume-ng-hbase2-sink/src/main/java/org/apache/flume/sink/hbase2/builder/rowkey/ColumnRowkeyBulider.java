package org.apache.flume.sink.hbase2.builder.rowkey;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.sink.hbase2.CloumnNotFoundException;
import org.apache.flume.sink.hbase2.builder.RowkeyBulider;

import java.io.UnsupportedEncodingException;

/**
 * 列值 rowkey构建器
 * 拼接列值作为rowkey
 */
public class ColumnRowkeyBulider implements RowkeyBulider {
    private String[] columns;
    private String split;

    public ColumnRowkeyBulider(String column,String split) {
        this.columns = column.split(",");
        this.split = split;
    }

    @Override
    public byte[] bulider(JSONObject jsonObject) throws UnsupportedEncodingException, CloumnNotFoundException {
        StringBuffer sb = new StringBuffer();
        for (String k : columns) {
            if (null == jsonObject.get(k)) {
                throw new CloumnNotFoundException("404", "The Cloumn " + k + " Not Found ");
            }
            sb.append(jsonObject.get(k)).append(split);
        }
        return sb.delete(sb.length()-1,sb.length()).toString().getBytes("UTF8");
    }



}
