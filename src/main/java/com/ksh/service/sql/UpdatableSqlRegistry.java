package com.ksh.service.sql;

import com.ksh.exception.SqlUpdateFailureException;

import java.util.Map;

public interface UpdatableSqlRegistry extends SqlRegistry{
    public void updateSql(String key, String sql) throws SqlUpdateFailureException;
    
    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;
}
