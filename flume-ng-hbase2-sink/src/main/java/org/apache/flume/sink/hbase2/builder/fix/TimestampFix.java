package org.apache.flume.sink.hbase2.builder.fix;

import org.apache.flume.sink.hbase2.builder.RowkeyFixBulider;

import java.io.UnsupportedEncodingException;

public class TimestampFix implements RowkeyFixBulider {

    @Override
    public byte[] bulider() throws UnsupportedEncodingException {
        return String.valueOf(System.currentTimeMillis()).getBytes("UTF8");
    }

    @Override
    public byte[] bulider(String time) throws UnsupportedEncodingException {
        return new byte[0];
    }
}
