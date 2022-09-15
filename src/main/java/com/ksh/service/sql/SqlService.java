package com.ksh.service.sql;

import com.ksh.exception.SqlRetrievalFailureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
