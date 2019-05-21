package com.hnbian.flume.sink.rmdb.executor;

import com.hnbian.flume.sink.rmdb.Executor;
import com.hnbian.flume.sink.rmdb.SessionFactory;
import oracle.jdbc.driver.OracleDriver;
import org.apache.ibatis.session.SqlSession;
import java.util.Map;

public class ExecutorOracle  implements Executor {
    public static SqlSession session;
    private final String statement = "com.hnbian.flume.sink.rmdb.dao.JSONMapper.saveToOracle";
    public ExecutorOracle(String user, String pwd, String url){
        System.out.println("ExecutorOracle init...");
        try{
            OracleDriver driver = new OracleDriver();
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
