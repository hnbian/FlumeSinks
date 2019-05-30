package org.apache.flume.sink.hbase2.builder.fix;

import org.apache.flume.sink.hbase2.builder.RowkeyFixBulider;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeFix implements RowkeyFixBulider {
    @Override
    public byte[] bulider() throws UnsupportedEncodingException {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdFormatter.format(nowTime).getBytes("UTF8");
    }

    @Override
    public byte[] bulider(String time) throws UnsupportedEncodingException {
        return new byte[0];
    }
}
