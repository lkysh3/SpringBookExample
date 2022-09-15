package com.ksh.service.sql;

import com.ksh.exception.SqlNotFoundException;
import com.ksh.exception.SqlRetrievalFailureException;

import javax.annotation.PostConstruct;

public class BaseSqlService implements SqlService {
    protected SqlReader sqlReader;
    protected SqlRegistry sqlRegistry;

    public void setSqlReader(SqlReader sqlReader){
        this.sqlReader = sqlReader;
    }

    public void setSqlRegistry(SqlRegistry sqlRegistry){
        this.sqlRegistry = sqlRegistry;
    }

    @PostConstruct
    public void loadSql(){
        this.sqlReader.read(this.sqlRegistry);
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        try{
            return sqlRegistry.findSql(key);
        }catch(SqlNotFoundException e){
            throw new SqlRetrievalFailureException(e.getMessage());
        }
    }
}
