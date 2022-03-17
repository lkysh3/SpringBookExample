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

    // 템플릿 : workWithStatementStrategy
    // 콜백 : StatementStrategy

    // 템플릿 : 고정된 형태 안에서 바꿀수 있는 부분을 넣어서 사용하는 코드
    // 콜백 : 템플릿에서 호출되기 위해 파라미터로 넘겨진 오브젝트(여기가 바뀌는 부분)
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

    public void executeSql(final String query) throws SQLException{
        this.workWithStatementStrategy(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement(query);
                return ps;
            }
        });
    }

    public void executeSqlWithParameter(final String query, final Object... args) throws SQLException {
        this.workWithStatementStrategy(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement(query);

                int length = args.length;

                for(int i = 0; i < length; i++){
                    if(args[i] instanceof Integer){
                        ps.setInt(i, Integer.parseInt(args[i].toString()));
                    }
                }
                return ps;
            }
        });
    }


}
