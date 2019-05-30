package org.apache.flume.sink.hbase2.builder.fix;

import org.apache.flume.sink.hbase2.builder.RowkeyFixBulider;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class RandomFix implements RowkeyFixBulider {

    @Override
    public byte[] bulider() throws UnsupportedEncodingException {
        return String.valueOf(new Random().nextLong()).getBytes("UTF8");
    }

    @Override
    public byte[] bulider(String time) throws UnsupportedEncodingException {
        return new byte[0];
    }
}
