package com.ksh.service.sql;

import com.ksh.exception.SqlNotFoundException;

public interface SqlRegistry {
    void registerSql(String key, String sql);
    String findSql(String key) throws SqlNotFoundException;
}
