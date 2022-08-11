package com.ksh.service;

import com.ksh.dao.UserDao;
import com.ksh.domain.Grade;
import com.ksh.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ksh.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.ksh.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml", "/applicationContext.xml"})
public class UserServiceTest {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserDao userDao;

    @Autowired
    DataSource dataSource;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    List<User> users;

    @Before
    public void setup(){
        users = Arrays.asList(
                new User("lkysh3", "곽성훈", "eksxp123", Grade.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "lkysh3@gmail.com"),
                new User("joytouch", "강명성", "p2", Grade.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "joytouch@naver.com"),
                new User("erwins", "신승한", "p3", Grade.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "erwins@nate.com"),
                new User("madnite1", "이상호", "p4", Grade.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "madnite1@yahoo.com"),
                new User("green", "오민규","p5", Grade.GOLD, 100, Integer.MAX_VALUE, "green@daum.net")
        );
    }

    @Test
    public void beanCheck(){
        assertThat(userService, is(notNullValue()));
    }

    @Test
    @DirtiesContext
    public void upgradeGrades() throws Exception{
        userDao.deleteAll();

        for (User user : users) {
            userDao.add(user);
        }

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);;

        userService.upgradeGrades();

        checkGradeUpgraded(users.get(0), false);
        checkGradeUpgraded(users.get(1), true);
        checkGradeUpgraded(users.get(2), false);
        checkGradeUpgraded(users.get(3), true);
        checkGradeUpgraded(users.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(), is(2));
        assertThat(request.get(0), is(users.get(1).getEmail()));
        assertThat(request.get(1), is(users.get(3).getEmail()));
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

    @Test
    public void upgradeAllOrNothing() throws Exception{
        UserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setDataSource(this.dataSource);
        testUserService.setTransactionManager(this.transactionManager);
        testUserService.setMailSender(this.mailSender);

        userDao.deleteAll();
        for(User user : users){
            userDao.add(user);
        }

        try{
            testUserService.upgradeGrades();
            fail("TestUserServiceException expected");
        }catch(TestUserServiceException ex){

        }

        checkGradeUpgraded(users.get(1), false);
    }

    static class TestUserServiceImpl extends UserServiceImpl {
        private String id;

        private TestUserServiceImpl(String id){
            this.id = id;
        }

        protected void upgradeGrade(User user){
            if(user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeGrade(user);
        }
    }

    static class TestUserServiceException extends RuntimeException{
    }

    static class MockMailSender implements MailSender{
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests(){
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            requests.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

        }
    }
}
