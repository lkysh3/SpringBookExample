package com.ksh.sql;

import com.ksh.exception.SqlUpdateFailureException;
import com.ksh.service.sql.EmbeddedDBSqlRegistry;
import com.ksh.service.sql.UpdatableSqlRegistry;
import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.object.SqlUpdate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class EmbeddedDBSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{
    EmbeddedDatabase db;

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:/sqlRegistrySchema.sql")
                .build();

        EmbeddedDBSqlRegistry embeddedDBSqlRegistry = new EmbeddedDBSqlRegistry();
        embeddedDBSqlRegistry.setDataSource(db);

        return embeddedDBSqlRegistry;
    }

    @After
    public void tearDown(){
        db.shutdown();
    }

    @Test
    public void transactionalUpdate(){
        checkFindResult("SQL1", "SQL2", "SQL3");

        Map<String, String> sqlmap = new HashMap<String, String>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY9999!@#$", "Modified9999");

        try{
            sqlRegistry.updateSql(sqlmap);
            fail();
        }catch(SqlUpdateFailureException e){

        }

        checkFindResult("SQL1", "SQL2", "SQL3");
    }
}
