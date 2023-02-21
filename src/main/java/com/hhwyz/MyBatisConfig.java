package com.hhwyz;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyBatisConfig {
    static SqlSessionFactory sqlSessionFactory;

    static {
        String resource = "mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Properties properties = new Properties();
        properties.put("user.home", System.getProperty("user.home"));
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, properties);
    }
}
