package org.apache.flume.sink.hbase2.builder.fix;

import org.apache.flume.sink.hbase2.HBase2SinkConfigurationConstants;
import org.apache.flume.sink.hbase2.builder.RowkeyFixBulider;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ColumnTimeFormatFix implements RowkeyFixBulider {
    private DateFormat format;

    public ColumnTimeFormatFix(String timeFormat) {
        format = new SimpleDateFormat(timeFormat);
    }


    @Override
    public byte[] bulider() throws UnsupportedEncodingException {
        return new byte[0];
    }

    @Override
    public byte[] bulider(String time) throws UnsupportedEncodingException {
        try{

            Date date = format.parse(time);
            SimpleDateFormat sdFormatter =
                    new SimpleDateFormat(HBase2SinkConfigurationConstants.COLUMN_FIX_TIME_FORMAT);
            return sdFormatter.format(date.getTime()).getBytes("UTF8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
