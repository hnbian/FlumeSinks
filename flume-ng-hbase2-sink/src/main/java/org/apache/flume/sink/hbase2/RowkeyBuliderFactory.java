package org.apache.flume.sink.hbase2;

import org.apache.flume.sink.hbase2.builder.RowkeyBulider;
import org.apache.flume.sink.hbase2.builder.rowkey.DefaultRowkeyBulider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RowkeyBuliderFactory {
    private static final Logger logger = LoggerFactory.getLogger(RowkeyBuliderFactory.class);

    static RowkeyBulider getRowkeyBulider(String rowkeyFormat){
        logger.info("rowkeyFormat:"+rowkeyFormat);

        if(!"uuid".equals(rowkeyFormat)){
            String []  rkColumns = rowkeyFormat.split("\\|");
            if(null != rkColumns && rkColumns.length == 1){ //如果 rowkey 格式化数组长度为1 则使用列值rowkey构建器
                //return new ColumnRowkeyBulider(rkColumns[0]);
            }else{
               /* String columns = rkColumns[1];
                String fixType = rkColumns[0].split(",")[0];
                String fixName = rkColumns[0].split(",")[1];
                RowkeyFixBulider rowkeyFixBulider = BuliderFactory.getRowkeyFixBulider(fixName);
                if(null != fixType&&"suffix".equals(fixType)){
                    return new SuffixColumnRowkeyBulider(rowkeyFixBulider,columns);
                }else{
                    return new PrefixColumnRowkeyBulider(rowkeyFixBulider,columns);
                }*/
            }
        }
        //默认使用uuid 作为rowkey
        return new DefaultRowkeyBulider();
    }
}
