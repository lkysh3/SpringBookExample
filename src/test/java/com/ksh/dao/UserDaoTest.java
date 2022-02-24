package com.ksh.dao;


import com.ksh.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.sql.SQLException;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml"})
public class UserDaoTest {

    @Test
    public void testUserDao() throws ClassNotFoundException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("lkysh3");
        user.setName("곽성훈");
        user.setPassword("eksxp123");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());

        if(!user.getName().equals(user2.getName())){
            System.out.println("테스트 실패 (name)");
        }else if(!user.getPassword().equals(user2.getPassword())){
            System.out.println("테스트 실패 (password)");
        }else{
            System.out.println("조회 테스트 성공");
        }
    }
}
