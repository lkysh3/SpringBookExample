package com.ksh.dao;

import com.ksh.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
    private DataSource dataSource;
    private JdbcContext jdbcContext;

    public UserDao() {
    }

//    public void setJdbcContext(JdbcContext jdbcContext){
//        this.jdbcContext = jdbcContext;
//    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;

        // set 메소드이면서 jdbcContext에 대한 생성과 DI를 수행한다.
        // UserDao가 JdbcContext 대신 DI를 받아서 JdbcContext에게 넘겨주는 역할만 한다.
        // 인터페이스를 사용해서 DI를 하는것이 정석이지만 둘이 항상 함께 사용되어야 하는 경우라면 클래스로 DI를 해도 괜찮다.
        // 이때는 jdbcContext를 싱글톤으로 만들수 없다.
        this.jdbcContext = new JdbcContext();
        this.jdbcContext.setDataSource(dataSource);
    }

    public void add(final User user) throws SQLException {
        // 메소드 내부에 선언한 내부 클래스. 이 메소드 안에서만 사용할 수 있다.
        // 내부 클래스는 선언된 메소드의 로컬 변수에 직접 접근이 가능하다. 메소드 파라미터도 로컬 변수다.
        // 그때 외부변수는 final로 선언되어 있어야 한다.
//        class AddStatement implements StatementStrategy{
//            @Override
//            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPassword());
//
//                return ps;
//            }
//        }

        // 익명 내부 클래스 방식
        // 생성과 동시에 인스턴스를 생성한다.
        // 이름이 없기 때문에 자신의 타입은 가질수 없고 인터페이스 타입의 변수에만 저장 가능하다.
//        StatementStrategy st = new StatementStrategy() {
//            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPassword());
//
//                return ps;
//            }
//        };
//        jdbcContextWithStatementStrategy(st);

        // 위의 코드를 간결하게 정리한 것.
        // 메소드 내에서 한번만 쓰고 재사용할일이 없다면 이렇게 정리해도 된다.
        this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());

                return ps;
            }
        });
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;

        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("ID"));
            user.setName(rs.getString("NAME"));
            user.setPassword(rs.getString("PASSWORD"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null)
            throw new EmptyResultDataAccessException(1);

        return user;
    }

    // 클라이언트 : deleteAll
    // 템플릿 : jdbcContext.workWithStatementStrategy
    // 콜백 : new StatementStrategy
    // 전략 패턴에서는 클라이언트가 전략 오브젝트(new StatementStrategy)를 만들고 컨텍스트(jdbcContext.workWithStatementStrategy)를 호출할때 넘겨준다.
    public void deleteAll() throws SQLException {
        this.jdbcContext.executeSql("delete from users");
    }



    // 리턴값이 있는 경우에는 어떻게 할지 생각
    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }

            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    // 컨텍스트 코드
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }

            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
