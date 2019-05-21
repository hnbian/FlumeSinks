package com.hnbian.flume.sink.test;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RmdbSinkTest {
    String resource = "mybatis-config.xml";
    String columns = "c1,c2,c3,c4,c5";
    String columnss = "\"c1\",\"c2\",\"c3\",\"c4\",\"c5\"";
    private static  String[] columnsArr;
    private SqlSession confSession;
    private SqlSession fileSession;

    @Test
    public void mysqlTest() {
        String host = "node1";
        String port = "3306";
        //String driver = "com.mysql.jdbc.Driver";
        String dbName = "test";
        String tableName = "flume_test";
        String urlParam = "?allowMultiQueries=true&useUnicode=yes&characterEncoding=UTF-8&serverTimezone=GMT%2b8&useSSL=false&autoReconnect=true";
        String url = new StringBuffer().append("jdbc:mysql://").append(host).append(":").append(port).append("/").append(dbName).append(urlParam).toString();
        String user = "root";
        String pwd = "root";

        try{
            InputStream inputmybatisstream = Resources.getResourceAsStream(resource);
            SqlSessionFactory filesqlSessionFactorys = new SqlSessionFactoryBuilder().build(inputmybatisstream);
            SqlSession fileSession = filesqlSessionFactorys.openSession();
            this.fileSession = fileSession;

            List<String> values = new ArrayList();
            Map map = new HashMap();
            values.add("999,119.675703,29.090039,864297600943080,222");
            values.add("777,119.675703,29.090039,864297600943080,222");

            map.put("tableName","t_flume_test");
            map.put("datas",values);

            //oracle 插入字段需要双引号
            map.put("columns",columns);


            Configuration configuration = new Configuration();
            DruidDataSource dataSource = new DruidDataSource();

            dataSource.setPassword(pwd);
            dataSource.setUsername(user);
            dataSource.setUrl(url);
            Driver driver = new com.mysql.jdbc.Driver();
            //OracleDriver driver = new OracleDriver();
            dataSource.setDriver(driver);
            TransactionFactory txFactory = new ManagedTransactionFactory();

            Environment environment = new Environment("development", txFactory, dataSource);

            InputStream inputStream = Resources.getResourceAsStream("JSONMapper.xml");
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, "JSONMapper.xml", configuration.getSqlFragments());
            mapperParser.parse();
            configuration.setEnvironment(environment);
            SqlSessionFactory confSqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            SqlSession confSession = confSqlSessionFactory.openSession();
            //this.confSession = confSession;

            /*confSession.insert("com.hnbian.flume.sink.rmdbtest.JSONMapper.saveToMysql", map);
            confSession.commit();*/

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void oracleTest() {
        String host = "node1";
        String port = "1521";
        String driver = "oracle.jdbc.driver.OracleDriver";
        String dbName = "test";
        String tableName = "t_flume_test";
        String url = "jdbc:oracle:thin:@node1:1521:test";
        String user = "root";
        String pwd = "root";


       try{
           InputStream inputmybatisstream = Resources.getResourceAsStream(resource);
           SqlSessionFactory filesqlSessionFactorys = new SqlSessionFactoryBuilder().build(inputmybatisstream);
           SqlSession fileSession = filesqlSessionFactorys.openSession();
           this.fileSession = fileSession;

           List<String> values = new ArrayList();
           Map map = new HashMap();
           values.add("999,119.675703,29.090039,864297600943080,222");
           values.add("777,119.675703,29.090039,864297600943080,222");


           map.put("tableName","test.\"t_flume_test\"");
           map.put("datas",values);

           //oracle 插入字段需要双引号
           map.put("columns",columnss);



           Configuration configuration = new Configuration();
           DruidDataSource dataSource = new DruidDataSource();

           dataSource.setPassword(pwd);
           dataSource.setUsername(user);
           dataSource.setUrl(url);
           Driver driver2 = new oracle.jdbc.driver.OracleDriver();
           dataSource.setDriver(driver2);
           TransactionFactory txFactory = new ManagedTransactionFactory();

           Environment environment = new Environment("development", txFactory, dataSource);

           InputStream inputStream = Resources.getResourceAsStream("JSONMapper.xml");
           XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, "JSONMapper.xml", configuration.getSqlFragments());
           mapperParser.parse();
           configuration.setEnvironment(environment);
           SqlSessionFactory confSqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
           SqlSession confSession = confSqlSessionFactory.openSession();
           /*confSession.insert("com.hnbian.flume.sink.rmdbtest.JSONMapper.saveToOracle", map);
            confSession.commit();*/

       }catch(Exception e){
           e.printStackTrace();
       }finally {
       }

        /*//oracle 插入字段需要双引号
        map.put("columns",columnss);*/
    }

    @Test
    public void postgresqlTest() {
        String host = "node1";
        String port = "5432";
        String driver = "org.postgresql.Driver";
        String dbName = "postgres";
        String tableName = "t_flume_test";
        String url = new StringBuffer().append("jdbc:postgresql://").append(host).append(":").append(port).append("/").append(dbName).toString();
        String user = "gpadmin";
        String pwd = "gpadmin";
    }
}
