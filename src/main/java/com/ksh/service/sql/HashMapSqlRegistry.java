package com.ksh.service.sql;

import com.ksh.exception.SqlNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class HashMapSqlRegistry implements SqlRegistry{
    private Map<String, String> sqlMap = new HashMap<String, String>();

    @Override
    public void registerSql(String key, String sql) {
        sqlMap.put(key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        try{
            return sqlMap.get(key);
        }catch(SqlNotFoundException e){
            throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
        }
    }
}
