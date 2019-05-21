package com.hnbian.flume.sink.rmdb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.hnbian.flume.sink.rmdb.executor.ExecutorMysql;
import com.hnbian.flume.sink.rmdb.executor.ExecutorOracle;
import com.hnbian.flume.sink.rmdb.executor.ExecutorPostGreSQL;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.*;
import org.apache.flume.conf.BatchSizeSupported;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RmdbSink extends AbstractSink implements Configurable, BatchSizeSupported {
    private static final Logger logger = LoggerFactory.getLogger(RmdbSink.class);
    private int batchSize = 100;
    private final CounterGroup counterGroup = new CounterGroup();
    private SinkCounter sinkCounter;
    //执行器
    private Executor executor;
    private String user;
    private String pwd;
    private String url;
    private String dbType;
    private String tableName;
    private String columns;
    private String[] columnsArr;
    private Map<String, Object> map = new HashMap();
    private List<String> values;

    /**
     * 从channel 中取出数据并对数据进行处理
     */
    @Override
    public Status process() throws EventDeliveryException {
        Status status = Status.READY;
        Channel channel = getChannel();
        Transaction txn = channel.getTransaction();
        try {
            txn.begin();
            int count;
            values = new ArrayList();
            for (count = 0; count < batchSize; ++count) {
                Event event = channel.take();
                if (event == null) {
                    break;
                }
                String body = new String(event.getBody());
                JSONObject eventJsonObject = JSON.parseObject(body);
                //logger.info("body=>" + body);
                if (null == columns) {
                    logger.info("columns init...");
                    Set<String> set = eventJsonObject.keySet();
                    columnsArr = set.toArray(new String[set.size()]);
                    if (!"MYSQL".equals(StringUtils.upperCase(dbType))) {
                        columns = new StringBuffer().append("\"").append(columns.join("\",\"", columnsArr)).append("\"").toString();
                    } else {
                        columns = columns.join(",", columnsArr);
                    }
                    logger.info("columns:"+columns);
                    map.put("columns", columns);
                }
                if (null == map.get("tableName")) {
                    logger.info("tableName init...");
                    map.put("tableName", tableName);
                }

                StringBuffer sb = new StringBuffer();
                for (String key : columnsArr) {
                    sb.append(eventJsonObject.getString(key)).append(",");
                }
                values.add(sb.deleteCharAt(sb.length() - 1).toString());
            }

            if (count <= 0) {
                sinkCounter.incrementBatchEmptyCount();
                counterGroup.incrementAndGet("channel.underflow");
                status = Status.BACKOFF;
            } else {
                if (count < batchSize) {
                    sinkCounter.incrementBatchUnderflowCount();
                    status = Status.BACKOFF;
                } else {
                    sinkCounter.incrementBatchCompleteCount();
                }

                logger.info("RmdbSink saveTo "+dbType+" "+count+" events");

                sinkCounter.addToEventDrainAttemptCount(count);
                map.put("datas", values);
                executor.execute(map);
            }
            txn.commit();
            sinkCounter.addToEventDrainSuccessCount(count);
            counterGroup.incrementAndGet("transaction.success");
        } catch (Throwable ex) {
            try {
                txn.rollback();
                counterGroup.incrementAndGet("transaction.rollback");
            } catch (Exception ex2) {
                logger.error(
                        "Exception in rollback. Rollback might not have been successful.",
                        ex2);
            }

            if (ex instanceof Error || ex instanceof RuntimeException) {
                logger.error("Failed to commit transaction. Transaction rolled back.", ex);
                Throwables.propagate(ex);
            } else {
                logger.error("Failed to commit transaction. Transaction rolled back.", ex);
                throw new EventDeliveryException("Failed to commit transaction. Transaction rolled back.", ex);
            }
        } finally {
            txn.close();
        }

        return status;
    }

    @Override
    public long getBatchSize() {
        return batchSize;
    }

    /**
     * 获取配置
     */
    @Override
    public void configure(Context context) {
        if (sinkCounter == null) {
            sinkCounter = new SinkCounter(getName());
        }
        try {
            if (null != context.getInteger("batchSize")) {
                batchSize = context.getInteger("batchSize");
            }
            if (StringUtils.isNotBlank(context.getString("dbType"))) {
                dbType = context.getString("dbType");
            }
            Preconditions.checkState(StringUtils.isNotBlank(dbType), "Missing Param: dbType");
            if (StringUtils.isNotBlank(context.getString("user"))) {
                user = context.getString("user");
            }
            Preconditions.checkState(StringUtils.isNotBlank(user), "Missing Param: user");
            if (StringUtils.isNotBlank(context.getString("pwd"))) {
                pwd = context.getString("pwd");
            }
            Preconditions.checkState(StringUtils.isNotBlank(pwd), "Missing Param: pwd");
            if (StringUtils.isNotBlank(context.getString("url"))) {
                url = context.getString("url");
            }
            Preconditions.checkState(StringUtils.isNotBlank(url), "Missing Param: url");
            if (StringUtils.isNotBlank(context.getString("tableName"))) {
                tableName = context.getString("tableName");
            }
            Preconditions.checkState(StringUtils.isNotBlank(tableName), "Missing Param: tableName");

            if ("MYSQL".equals(StringUtils.upperCase(dbType))) {
                executor = new ExecutorMysql(user, pwd, url);
            } else if ("ORACLE".equals(StringUtils.upperCase(dbType))) {
                executor = new ExecutorOracle(user, pwd, url);
            } else if ("POSTGRESQL".equals(StringUtils.upperCase(dbType))) {
                executor = new ExecutorPostGreSQL(user, pwd, url);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        logger.info("OracleSink {} started");
        sinkCounter.start();
        try {
            sinkCounter.incrementConnectionCreatedCount();
        } catch (Exception ex) {
            ex.printStackTrace();
            sinkCounter.incrementConnectionFailedCount();
        }
        super.start();
    }

    @Override
    public void stop() {
        logger.info("OracleSink {} stopping");
        sinkCounter.incrementConnectionClosedCount();
        sinkCounter.stop();
        super.stop();
    }
}
