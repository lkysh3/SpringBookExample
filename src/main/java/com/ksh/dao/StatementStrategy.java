package com.ksh.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public interface StatementStrategy {
    PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
