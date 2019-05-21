package com.hnbian.flume.sink.rmdb;

import java.util.Map;

public interface Executor {
    void execute(Map<String, Object> map);

}
