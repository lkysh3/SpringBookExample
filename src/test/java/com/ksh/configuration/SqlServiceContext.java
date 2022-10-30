package com.ksh.configuration;

import com.ksh.dao.UserDao;
import com.ksh.service.sql.EmbeddedDBSqlRegistry;
import com.ksh.service.sql.OxmSqlService;
import com.ksh.service.sql.SqlRegistry;
import com.ksh.service.sql.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@Configuration
public class SqlServiceContext {
    @Autowired
    SqlMapConfig sqlMapConfig;

    @Bean
    public SqlService sqlService(){
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        sqlService.setSqlMap(this.sqlMapConfig.getSqlMapResource());

        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry(){
        EmbeddedDBSqlRegistry sqlRegistry = new EmbeddedDBSqlRegistry();
        sqlRegistry.setDataSource(embeddedDatabase());

        return sqlRegistry;
    }

    @Bean
    public Unmarshaller unmarshaller(){
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setContextPath("com.ksh.service.jaxb");

        return unmarshaller;
    }

    @Bean
    public DataSource embeddedDatabase(){
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:/sqlRegistrySchema.sql")
                .build();
    }
}
