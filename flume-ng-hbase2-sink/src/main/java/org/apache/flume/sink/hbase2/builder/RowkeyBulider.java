package org.apache.flume.sink.hbase2.builder;

import com.alibaba.fastjson.JSONObject;
import org.apache.flume.sink.hbase2.CloumnNotFoundException;

import java.io.UnsupportedEncodingException;

/**
 * 构建rowkey 接口
 */
public interface RowkeyBulider {
    byte[] bulider(JSONObject jsonObject) throws UnsupportedEncodingException,CloumnNotFoundException;
}
