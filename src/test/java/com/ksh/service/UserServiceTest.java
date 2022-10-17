package com.ksh.service;

import com.ksh.dao.UserDao;
import com.ksh.domain.Grade;
import com.ksh.domain.User;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ksh.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.ksh.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Rollback(false)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml", "/test-applicationContext.xml"})
public class UserServiceTest {
    @Autowired
    ApplicationContext context;

    @Autowired
    UserService userService;

    @Autowired
    UserService testUserService;

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
    public void mockUpgradeGrades() throws Exception{
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeGrades();

        verify(mockUserDao, times(2)).update(Matchers.any(User.class));
        verify(mockUserDao, times(2)).update(Matchers.any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getGrade(), is(Grade.SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getGrade(), is(Grade.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
    }

    @Test
    @DirtiesContext
    public void upgradeGrades() throws Exception{
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeGrades();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(), is(2));
        checkUserAndGrade(updated.get(0), "joytouch", Grade.SILVER);
        checkUserAndGrade(updated.get(1), "madnite1", Grade.GOLD);

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

    private void checkUserAndGrade(User updated, String expectedId, Grade expectedGrade){
        assertThat(updated.getId(), is(expectedId));
        assertThat(updated.getGrade(), is(expectedGrade));
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
    public void advisorAutoProxyCreator() throws NoSuchMethodException{
        // testUserService가 프록시인지 확인
        assertThat(testUserService, instanceOf(java.lang.reflect.Proxy.class));

        //System.out.println(Target.class.getMethod("minus", int.class, int.class));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        userDao.deleteAll();
        for (User user : users) {
            userDao.add(user);
        }

        try {
            this.testUserService.upgradeGrades();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException ex) {

        }

        checkGradeUpgraded(users.get(1), true);
    }

    //@Test(expected=TransientDataAccessException.class)
    @Test
    public void readOnlyTransactionAttribute() {
        try {
            testUserService.getAll();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void transactionSync(){
//        userService.deleteAll();
//        // deleteAll 트랜잭션 종료
//
//        userService.add(users.get(0));
//        // add 트랜잭션 종료
//        userService.add(users.get(1));
//        // add 트랜잭션 종료
//        // 3개의 트랜잭션이 생성되었다.

//        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
//        // 오라클 DB를 사용하는 경우 readonly 속성은 무시된다.
//        txDefinition.setReadOnly(true);
//        // 타임아웃으로 테스트. deleteAll이 새로 트랜잭션을 생성하지 않고 생성된 트랜잭션에 참여했기 때문에 타임아웃이 적용된다.
//        txDefinition.setTimeout(5);
//        // 트랜잭션 시작
//        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
//
//        // 이미 트랜잭션이 시작중이고 전파속성이 REQUIRED 이므로 현재 트랜잭션에 참여
//        userService.deleteAll();
//
//        // 이미 트랜잭션이 시작중이고 전파속성이 REQUIRED 이므로 현재 트랜잭션에 참여
//        userService.add(users.get(0));
//        // 이미 트랜잭션이 시작중이고 전파속성이 REQUIRED 이므로 현재 트랜잭션에 참여
//        userService.add(users.get(1));
//
//        transactionManager.commit(txStatus);
//        // 트랜잭션 종료. 1개의 트랜잭션만 생성되어 작업이 완료됨.


//        // 트랜잭션 시작전에 0으로 초기화
//        userService.deleteAll();
//        assertThat(userDao.getCount(), is(0));
//
//        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
//        // 트랜잭션 시작
//        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
//
//        // 이미 트랜잭션이 시작중이고 전파속성이 REQUIRED 이므로 현재 트랜잭션에 참여
//        userService.add(users.get(0));
//        // 이미 트랜잭션이 시작중이고 전파속성이 REQUIRED 이므로 현재 트랜잭션에 참여
//        userService.add(users.get(1));
//
//        assertThat(userDao.getCount(), is(2));
//
//        transactionManager.rollback(txStatus);
//
//        // 트랜잭션을 롤백했으니까 0으로 돌아가야 한다.
//        assertThat(userDao.getCount(), is(0));


        // 테스트 메소드가 트랜잭션의 경계가 되어 메소드에서 실행되는 내용은 동일한 트랜잭션으로 실행된다.
        userService.deleteAll();
        userService.add(users.get(0));
        userService.add(users.get(1));
    }

    static class TestUserService extends UserServiceImpl {
        private String id = "madnite1";

        protected void upgradeGrade(User user){
            if(user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeGrade(user);
        }

        public List<User> getAll(){
            for(User user : super.getAll()){
                super.update(user);
            }
            return null;
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

    static class MockUserDao implements UserDao{
        private List<User> users;
        private List<User> updated = new ArrayList();

        private MockUserDao(List<User> users){
            this.users = users;
        }

        // 등급 업그레이드 대상 사용자 확인용
        public List<User> getUpdated(){
            return this.updated;
        }

        // 스텁 기능 제공
        @Override
        public List<User> getAll() {
            return this.users;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public void add(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User get(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            throw new UnsupportedOperationException();
        }


    }
}
