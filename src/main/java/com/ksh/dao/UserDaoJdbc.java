package com.ksh.dao;

import com.ksh.domain.Grade;
import com.ksh.domain.User;
import com.ksh.exception.DuplicateUserIdException;
import com.ksh.service.sql.SqlService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
//    private String sqlAdd;
//    private String sqlGet;
//    private String sqlGetAll;
//    private String sqlDeleteAll;
//    private String sqlGetCount;
//    private String sqlUpdate;

//    private Map<String, String > sqlMap;

    private SqlService sqlService;

    private final RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setId(resultSet.getString("id"));
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));
            user.setGrade(Grade.valueOf(resultSet.getInt("grade")));
            user.setLogin(resultSet.getInt("login"));
            user.setRecommend(resultSet.getInt("recommend"));
            user.setEmail(resultSet.getString("email"));
            return user;
        }
    };

    public UserDaoJdbc() {
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

//    public void setSqlAdd(String sqlAdd){
//        this.sqlAdd = sqlAdd;
//    }
//
//    public void setSqlGet(String sqlGet){
//        this.sqlGet = sqlGet;
//    }
//
//    public void setSqlGetAll(String sqlGetAll){
//        this.sqlGetAll = sqlGetAll;
//    }
//
//    public void setSqlDeleteAll(String sqlDeleteAll){
//        this.sqlDeleteAll = sqlDeleteAll;
//    }
//
//    public void setSqlGetCount(String sqlGetCount){
//        this.sqlGetCount = sqlGetCount;
//    }
//
//    public void setSqlUpdate(String sqlUpdate){
//        this.sqlUpdate = sqlUpdate;
//    }


//    public void setSqlMap(Map<String, String> sqlMap){
//        this.sqlMap = sqlMap;
//    }

    public void setSqlService(SqlService sqlService){
        this.sqlService = sqlService;
    }

    public void add(final User user) throws DuplicateUserIdException {
        this.jdbcTemplate.update(this.sqlService.getSql("userAdd"),
                user.getId(), user.getName(), user.getPassword(), user.getGrade().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"), new Object[]{id}, userMapper);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
    }

    public int getCount() {
        // queryForInt는 Deprecated 되었음.
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGetCount"), Integer.class);
    }

    public void update(User user) {
        this.jdbcTemplate.update(this.sqlService.getSql("userUpdate"),
                user.getName(), user.getPassword(), user.getGrade().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
    }
}