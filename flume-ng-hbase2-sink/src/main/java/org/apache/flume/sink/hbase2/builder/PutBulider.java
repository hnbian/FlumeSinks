package org.apache.flume.sink.hbase2.builder;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.client.Put;

import java.util.Map;

public interface PutBulider {

    Put bulidPut(JSONObject jsonObject, Map<String, String> headers);
}
