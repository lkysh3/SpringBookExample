package com.ksh.dao;


import com.ksh.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml", "/applicationContext.xml"})
public class UserDaoTest {
//    @Autowired
//    private ApplicationContext context;

    @Autowired
    UserDao dao;

    User user1;
    User user2;
    User user3;

    @Before
    public void setup(){
        // xml을 이용한 방식
        // xml 파일을 구성정보로 사용한다.
//        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

        // DaoFactory를 사용하는 방식
        // Configuration 어노테이션이 붙은 클래스를 구성정보로 사용한다.
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

//        this.dao = this.context.getBean("userDao", UserDao.class);

        user1 = new User("lkysh3", "곽성훈", "eksxp123");
        user2 = new User("leegw70", "이길원", "springno2");
        user3 = new User("bumjin", "박범진", "springno3");

//        System.out.println(this.context);
//        System.out.println(this);
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
        assertThat(userget1.getName(), is(userget1.getName()));
        assertThat(userget1.getPassword(), is(userget1.getPassword()));

        User userget2 = dao.get(user2.getId());
        assertThat(userget2.getName(), is(userget2.getName()));
        assertThat(userget2.getPassword(), is(userget2.getPassword()));
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
}
