package org.apache.flume.sink.hbase2.builder.fix;

import org.apache.flume.sink.hbase2.HBase2SinkConfigurationConstants;
import org.apache.flume.sink.hbase2.builder.RowkeyFixBulider;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ColumnTimeStampFix implements RowkeyFixBulider {
    @Override
    public byte[] bulider() throws UnsupportedEncodingException {
        return new byte[0];
    }

    @Override
    public byte[] bulider(String time) throws UnsupportedEncodingException {
        Long timestamp ;
        if(time.length() == 10){
            timestamp = Long.parseLong(time)*1000;
        }else{
            timestamp = Long.parseLong(time);
        }
        Date nowTime = new Date(timestamp);
        SimpleDateFormat sdFormatter = new SimpleDateFormat(HBase2SinkConfigurationConstants.COLUMN_FIX_TIME_FORMAT);
        return sdFormatter.format(nowTime).getBytes("UTF8");
    }
}
