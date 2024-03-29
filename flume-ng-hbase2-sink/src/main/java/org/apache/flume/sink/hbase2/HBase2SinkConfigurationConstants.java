/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.hbase2;

import org.apache.hadoop.hbase.HConstants;

/**
 * Constants used for configuration of HBaseSink2
 *
 */
public class HBase2SinkConfigurationConstants {
  /**
   * The HBase table which the sink should write to.
   */
  public static final String CONFIG_TABLE = "table";
  /**
   * The column family which the sink should use.
   */
  public static final String CONFIG_COLUMN_FAMILY = "columnFamily";
  /**
   * Maximum number of events the sink should take from the channel per
   * transaction, if available.
   */
  public static final String CONFIG_BATCHSIZE = "batchSize";
  /**
   * The fully qualified class name of the serializer the sink should use.
   */
  public static final String CONFIG_SERIALIZER = "serializer";
  /**
   * Configuration to pass to the serializer.
   */
  public static final String CONFIG_SERIALIZER_PREFIX = CONFIG_SERIALIZER + ".";

  public static final String CONFIG_TIMEOUT = "timeout";
  public static final String ROWKEY = "rowkeyFormat";

  public static final String CONFIG_ENABLE_WAL = "enableWal";

  public static final boolean DEFAULT_ENABLE_WAL = true;

  public static final long DEFAULT_TIMEOUT = 60000;

  public static final String CONFIG_KEYTAB = "kerberosKeytab";

  public static final String CONFIG_PRINCIPAL = "kerberosPrincipal";

  public static final String ZK_QUORUM = "zookeeperQuorum";

  public static final String ZK_ZNODE_PARENT = "znodeParent";

  public static final String DEFAULT_ZK_ZNODE_PARENT =
      HConstants.DEFAULT_ZOOKEEPER_ZNODE_PARENT;

  public static final String CONFIG_COALESCE_INCREMENTS = "coalesceIncrements";

  public static final Boolean DEFAULT_COALESCE_INCREMENTS = false;

  public static final int DEFAULT_MAX_CONSECUTIVE_FAILS = 10;

  public static final String CONFIG_MAX_CONSECUTIVE_FAILS = "maxConsecutiveFails";
  /**
   * 前缀/后缀
   */
  public static final String ROWKEY_FIX = "rowkey.fix";
  /**
   * 固定前后缀类型
   */
  public static final String ROWKEY_FIX_TYPE = "rowkey.fix.type";
  /**
   * 时间列格式
   */
  public static final String ROWKEY_FIX_COLUMN_FORMAT = "rowkey.fix.column.format";

    /**
     * 取时间格式的列名
     */
    public static final String ROWKEY_FIX_COLUMN_NAME = "rowkey.fix.column.name";

  /**
   * rowkey 拼装列
   */
  public static final String ROWKEY_COLUMNS = "rowkey.columns";
  /**
   * rowkey 分隔符
   */
  public static final String ROWKEY_SPLIT = "rowkey.split";
  /**
   * 是否将header保存到Hbase中。 默认false
   */
  public static final String SAVE_HEADER = "saveHeader";

  public static final String COLUMN_FIX_TIME_FORMAT = "yyyyMMddHHmmssSSS"; //yyyyMMddHHmmssSSSS
}
