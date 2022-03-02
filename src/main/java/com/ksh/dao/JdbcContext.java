package com.ksh.dao;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class JdbcContext {
    private DataSource dataSource;

    // DI 받을수 있게 준비
    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException{
        Connection c = null;
        PreparedStatement ps = null;

        try{
            c = this.dataSource.getConnection();
            ps = stmt.makePreparedStatement(c);
            ps.executeUpdate();
        }catch(SQLException e){
            throw e;
        }finally{
            if(ps != null){
                try{
                    ps.close();
                }catch(SQLException e) {
                }
            }

            if(c != null){
                try{
                    c.close();
                }catch(SQLException e) {
                }
            }
        }
    }
}
