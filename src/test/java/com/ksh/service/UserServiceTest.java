package com.ksh.service;

import com.ksh.dao.UserDao;
import com.ksh.domain.Grade;
import com.ksh.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

import static com.ksh.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.ksh.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml", "/applicationContext.xml"})
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    List<User> users;

    @Before
    public void setup(){
        users = Arrays.asList(
                new User("lkysh3", "곽성훈", "eksxp123", Grade.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("joytouch", "강명성", "p2", Grade.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", Grade.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
                new User("madnite1", "이상호", "p4", Grade.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new User("green", "오민규","p5", Grade.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void beanCheck(){
        assertThat(userService, is(notNullValue()));
    }

    @Test
    public void upgradeGrades(){
        userDao.deleteAll();

        for (User user : users) {
            userDao.add(user);
        }

        userService.upgradeGrades();

        checkGradeUpgraded(users.get(0), false);
        checkGradeUpgraded(users.get(1), true);
        checkGradeUpgraded(users.get(2), false);
        checkGradeUpgraded(users.get(3), true);
        checkGradeUpgraded(users.get(4), false);
    }

    private void checkGradeUpgraded(User user, boolean upgraded){
        User userUpdate = userDao.get(user.getId());
        if(upgraded){
            assertThat(userUpdate.getGrade(), is(user.getGrade().nextGrade()));
        }else{
            assertThat(userUpdate.getGrade(), is(user.getGrade()));
        }
    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWithGrade = users.get(4);
        User userWithoutGrade = users.get(0);
        userWithoutGrade.setGrade(null);

        userService.add(userWithGrade);
        userService.add(userWithoutGrade);

        User userWithGradeRead = userDao.get(userWithGrade.getId());
        User userWithoutGradeRead = userDao.get(userWithoutGrade.getId());

        assertThat(userWithGrade.getGrade(), is(userWithGradeRead.getGrade()));
        assertThat(userWithoutGradeRead.getGrade(), is(Grade.BASIC));
    }
}
