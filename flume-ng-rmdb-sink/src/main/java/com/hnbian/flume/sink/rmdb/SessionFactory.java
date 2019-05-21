package com.hnbian.flume.sink.rmdb;

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

import java.io.InputStream;
import java.sql.Driver;

public class SessionFactory {
    public static SqlSession getSession(Driver driver, String user, String pwd, String url) {
        try {
            Configuration configuration = new Configuration();
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setPassword(pwd);
            dataSource.setUsername(user);
            dataSource.setUrl(url);
            dataSource.setDriver(driver);
            TransactionFactory txFactory = new ManagedTransactionFactory();

            Environment environment = new Environment("development", txFactory, dataSource);

            InputStream inputStream = Resources.getResourceAsStream("JSONMapper.xml");
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, "JSONMapper.xml", configuration.getSqlFragments());
            mapperParser.parse();
            configuration.setEnvironment(environment);
            SqlSessionFactory confSqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            SqlSession confSession = confSqlSessionFactory.openSession();
            return confSession;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
