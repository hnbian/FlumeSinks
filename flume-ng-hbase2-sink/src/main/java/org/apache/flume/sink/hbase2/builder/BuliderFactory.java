package org.apache.flume.sink.hbase2.builder;

import org.apache.commons.lang.StringUtils;
import org.apache.flume.sink.hbase2.builder.fix.*;
import org.apache.flume.sink.hbase2.builder.put.PutWithHeaderBulider;
import org.apache.flume.sink.hbase2.builder.put.PutWithOutHeaderBulider;
import org.apache.flume.sink.hbase2.builder.rowkey.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuliderFactory {
    private static final Logger logger = LoggerFactory.getLogger(BuliderFactory.class);

    public static RowkeyFixBulider getRowkeyFixBulider(String fixType, String rowkeyFixColumnFormat) {
        switch (fixType) {
            case "timestamp":
                return new TimestampFix();
            case "random":
                return new RandomFix();
            case "nano":
                return new NanoTimestampFix();
            case "currentTime":
                return new CurrentTimeFix();
            case "columnTime":
                if(StringUtils.equals("timeStamp",rowkeyFixColumnFormat)){
                    return new ColumnTimeStampFix();
                }else{
                    return new ColumnTimeFormatFix(rowkeyFixColumnFormat);
                }
            default:
                return new UUIDFix();
        }
    }


    /**
     * Rowkey bulider 工厂方法
     *
     * @param rowkeyFix             固定类型 前缀（prefix）/后缀(suffix)
     * @param rowkeyFixType
     * @param rowkeyFixColumnFormat
     * @param rowkeyColumns
     * @param rowkeySplit
     * @return
     */
    public static RowkeyBulider getRowkeyBulider
    (String rowkeyFix, String rowkeyFixType, String rowkeyFixColumnFormat,String rowkeyFixColumnName,
     String rowkeyColumns, String rowkeySplit) {
        if(null == rowkeyFix && null != rowkeyColumns){ //只使用列值作为rowkey
            return new ColumnRowkeyBulider(rowkeyColumns,rowkeySplit);
        }else if(null != rowkeyFix){
            RowkeyFixBulider rowkeyFixBulider = getRowkeyFixBulider(rowkeyFixType,rowkeyFixColumnFormat);
            logger.info("rowkeyFixBulider.getClass()=> "+rowkeyFixBulider.getClass());
            if(StringUtils.equals("columnTime",rowkeyFixType)){
                if("suffix".equals(rowkeyFix)){
                    return new SuffixColumnTimeRowkeyBulider(rowkeyFixBulider,rowkeyColumns,rowkeyFixColumnName,rowkeySplit);
                }else{
                    return new PrefixColumnTimeRowkeyBulider(rowkeyFixBulider,rowkeyColumns,rowkeyFixColumnName,rowkeySplit);
                }
            }else if("suffix".equals(rowkeyFix)){
                return new SuffixColumnRowkeyBulider(rowkeyFixBulider,rowkeyColumns,rowkeySplit);
            }else{
                return new PrefixColumnRowkeyBulider(rowkeyFixBulider,rowkeyColumns,rowkeySplit);
            }
        }
        return new DefaultRowkeyBulider();
    }

    /**
     * Put 构建器工厂方法
     *
     * @param saveHeader    是否保存Header
     * @param rowkeyBulider rowkey 构建器
     * @param cf            列族名称
     * @return
     */
    public static PutBulider getPutBulider(Boolean saveHeader, RowkeyBulider rowkeyBulider, byte[] cf) {
        if (saveHeader) {
            return new PutWithHeaderBulider(rowkeyBulider, cf);
        } else {
            return new PutWithOutHeaderBulider(rowkeyBulider, cf);
        }
    }

}
