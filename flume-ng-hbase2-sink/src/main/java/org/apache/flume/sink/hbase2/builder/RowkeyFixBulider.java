package org.apache.flume.sink.hbase2.builder;

import java.io.UnsupportedEncodingException;

public interface RowkeyFixBulider {
    byte[] bulider() throws UnsupportedEncodingException;

    byte[] bulider(String time) throws UnsupportedEncodingException;


}
