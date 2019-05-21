package com.hnbian.flume.sink.test;

import java.util.Map;


public interface JSONMapper {
    //void save(@Param("tableName") String tableName, @Param("columns") String columns, @Param("datas") List<String> datas);
    void saveToMysql(Map map);
    void saveToOracle(Map map);
    void saveToPostgreSql(Map map);
}
