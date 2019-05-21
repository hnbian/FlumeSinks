package com.hnbian.flume.sink.rmdb.executor;

import com.hnbian.flume.sink.rmdb.Executor;
import com.hnbian.flume.sink.rmdb.SessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.sql.Driver;
import java.util.Map;

public class ExecutorMysql implements Executor {
    public static SqlSession session;
    private final String statement = "com.hnbian.flume.sink.rmdb.dao.JSONMapper.saveToMysql";
    public ExecutorMysql(String user, String pwd, String url){
        System.out.println("ExecutorMysql init...");
        try{
            Driver driver = new com.mysql.jdbc.Driver();
            session = SessionFactory.getSession(driver,user,pwd ,url);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void execute(Map<String, Object> map) {
        if(null != session){
            session.insert(statement,map);
            session.commit();
        }
    }
}
