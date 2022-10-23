package com.ksh.studytest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class EmbeddedDBTest {
    EmbeddedDatabase db;
    JdbcTemplate template;

    @Before
    public void setUp(){
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:/schema.sql")
                .addScript("classpath:/data.sql")
                .build();

        template = new JdbcTemplate(db);
    }

    @After
    public void tearDown(){
        db.shutdown();
    }

    @Test
    public void initData(){
        assertThat(template.queryForObject("select count(*) from sqlmap", Integer.class), is(2));

        List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
        assertThat((String)list.get(0).get("key_"), is("KEY1"));
        assertThat((String)list.get(0).get("sql_"), is("SQL1"));
        assertThat((String)list.get(1).get("key_"), is("KEY2"));
        assertThat((String)list.get(1).get("sql_"), is("SQL2"));

        for(Map<String, Object> obj : list){
            System.out.println(obj.toString());
        }
    }

    @Test
    public void insert(){
        template.update("insert into sqlmap(key_, sql_) values(?, ?)", "KEY3", "SQL3");

        assertThat(template.queryForObject("select count(*) from sqlmap", Integer.class), is(3));

        List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
        for(Map<String, Object> obj : list){
            System.out.println(obj.toString());
        }
    }
}
