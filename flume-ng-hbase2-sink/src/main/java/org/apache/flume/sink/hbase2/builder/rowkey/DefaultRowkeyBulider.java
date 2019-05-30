package org.apache.flume.sink.hbase2.builder.rowkey;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.sink.hbase2.builder.RowkeyBulider;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 默认rowkey 构建器 使用uuid作为rowkey
 */
public class DefaultRowkeyBulider implements RowkeyBulider {

    @Override
    public byte[] bulider(JSONObject jsonObject)  throws UnsupportedEncodingException {
        return UUID.randomUUID().toString().getBytes("UTF8");
    }
}
