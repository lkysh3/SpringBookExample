package com.ksh.configuration;

import com.ksh.dao.UserDao;
import com.ksh.mail.DummyMailSender;
import com.ksh.service.UserService;
import com.ksh.service.UserServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@ComponentScan(basePackages = "com.ksh")
@EnableTransactionManagement
@EnableSqlService
//@Import(value= SqlServiceContext.class)
@PropertySource("classpath:jdbc.properties")
public class AppContext implements SqlMapConfig{
//    @Autowired
//    Environment env;

    @Value("${db.driverClass}")
    Class<? extends Driver> driverClass;

    @Value("${db.url}")
    String url;

    @Value("${db.username}")
    String username;

    @Value("${db.password}")
    String password;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(this.driverClass);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);

//        try{
//            dataSource.setDriverClass((Class<? extends java.sql.Driver>)Class.forName(env.getProperty("db.driverClass")));
//        }catch(ClassNotFoundException e){
//            throw new RuntimeException();
//        }
//
//        dataSource.setUrl(env.getProperty("db.url"));
//        dataSource.setUsername(env.getProperty("db.username"));
//        dataSource.setPassword(env.getProperty("db.password"));

//        dataSource.setDriverClass(OracleDriver.class);
//        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
//        dataSource.setUsername("SpringExample");
//        dataSource.setPassword("eksxp123");

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(){
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());

        return tm;
    }

    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("/sqlmap.xml", UserDao.class);
    }

    @Configuration
    @Profile("prod")
    public static class ProductionAppContext {
        @Bean
        public MailSender mailSender(){
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            return mailSender;
        }
    }

    @Configuration
    @Profile("test")
    public static class TestAppContext {
        @Bean
        public UserService testUserService(){
            return new UserServiceTest.TestUserService();
        }

        @Bean
        public MailSender mailSender(){
            return new DummyMailSender();
        }
    }

}
