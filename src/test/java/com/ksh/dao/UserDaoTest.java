package com.ksh.dao;


import com.ksh.configuration.AppContext;
import com.ksh.domain.Grade;
import com.ksh.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


@WebAppConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml", "/test-applicationContext.xml"})
@ContextConfiguration(classes= AppContext.class)
public class UserDaoTest {
//    @Autowired
//    private ApplicationContext context;

    @Autowired
    UserDao dao;
    @Autowired
    DataSource dataSource;

    User user1;
    User user2;
    User user3;

    @Autowired
    DefaultListableBeanFactory bf;

    @Test
    public void beans(){
        for(String n : bf.getBeanDefinitionNames()){
            System.out.println(n + "\t " + bf.getBean(n).getClass().getName());
        }
    }

    @Before
    public void setup(){
        // xml을 이용한 방식
        // xml 파일을 구성정보로 사용한다.
//        ApplicationContext context = new GenericXmlApplicationContext("test-applicationContext.xml");

        // DaoFactory를 사용하는 방식
        // Configuration 어노테이션이 붙은 클래스를 구성정보로 사용한다.
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

//        this.dao = this.context.getBean("userDao", UserDao.class);

        // 픽스쳐
        user1 = new User("lkysh3", "곽성훈", "eksxp123", Grade.BASIC, 1, 0, "lkysh3@gmail.com");
        user2 = new User("leegw70", "이길원", "springno2", Grade.SILVER, 55, 10, "leegw70@naver.com");
        user3 = new User("bumjin", "박범진", "springno3", Grade.GOLD, 100, 40, "bumjin@nate.com");

    }

    @Test
    public void addAngGet() throws ClassNotFoundException, SQLException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        checkSameUser(userget1, user1);

        User userget2 = dao.get(user2.getId());
        checkSameUser(userget2, user2);
    }

    @Test
    public void getCountTest() throws SQLException{
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));
    }

    @Test(expected= EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException{
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown_id");
    }

    @Test
    public void getAll(){
        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(1));
        checkSameUser(user2, users2.get(0));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users3.get(2));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(0));
    }

    // User 클래스의 변수값들이 서로 같은지 비교
    public void checkSameUser(User user1, User user2){
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getGrade(), is(user2.getGrade()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
        assertThat(user1.getEmail(), is(user2.getEmail()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey(){
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void sqlExceptionTranslate(){
        dao.deleteAll();

        try{
            dao.add(user1);
            dao.add(user1);
        }catch(DuplicateKeyException ex){
            SQLException sqlException = (SQLException)ex.getCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

            assertThat(set.translate(null, null, sqlException), instanceOf(DuplicateKeyException.class));
        }
    }

    @Test
    public void update(){
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setGrade(Grade.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        user1.setEmail("springno6@spring.com");

        dao.update(user1);

        User user1update = dao.get(user1.getId());
        checkSameUser(user1update, user1);
        User user2same = dao.get(user2.getId());
        checkSameUser(user2same, user2);
    }
}
