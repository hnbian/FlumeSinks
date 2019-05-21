package com.hnbian.flume.sink.rmdb.dao;

import java.util.Map;

public interface JSONMapper {
    void saveToMysql(Map map);
    void saveToOracle(Map map);
    void saveToPostgreSql(Map map);
}
