package com.ksh.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(oracle.jdbc.driver.OracleDriver.class);
//        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
//        dataSource.setUsername("SpringExample");
//        dataSource.setPassword("eksxp123");

        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
        dataSource.setUsername("springtestdb");
        dataSource.setPassword("eksxp123");

        return dataSource;
    }
}
