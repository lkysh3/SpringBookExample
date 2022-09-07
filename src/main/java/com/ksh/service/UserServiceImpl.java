package com.ksh.service;

import com.ksh.dao.UserDao;
import com.ksh.domain.Grade;
import com.ksh.domain.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.List;

public class UserServiceImpl implements UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;
//    UserGradeUpgradePolicy userGradeUpgradePolicy;
    PlatformTransactionManager transactionManager;

    private DataSource dataSource;
    private MailSender mailSender;


    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

//    public void setUserGradeUpgradePolicy(UserGradeUpgradePolicy userGradeUpgradePolicy){
//        this.userGradeUpgradePolicy = userGradeUpgradePolicy;
//    }

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    public void setMailSender(MailSender mailSender){
        this.mailSender = mailSender;
    }

    public void upgradeGrades() {
        List<User> users = userDao.getAll();

        for (User user : users) {
            if (canUpgradeLevel(user)) {
                upgradeGrade(user);
            }
        }
    }

    protected void upgradeGrade(User user){
        user.upgradeGrade();
        userDao.update(user);
        sendUpgradeEmail(user);

//        userGradeUpgradePolicy.upgradeGrade(user);
    }

    public void add(User user) {
        if(user.getGrade() == null){
            user.setGrade(Grade.BASIC);
        }

        userDao.add(user);
    }

    // UserService를 거쳐 userDao의 기능을 이용한다.
    // UserService에 트랜잭션 경계를 설정할 경우 UserDao의 데이터 조작에 모두 트랜잭션을 적용할 수 있다.
    // 필요한 부가로직을 추가할 수 있다.
    @Override
    public User get(String id) {
        return userDao.get(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public void deleteAll() {
        userDao.deleteAll();
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    private boolean canUpgradeLevel(User user){
        Grade currentGrade = user.getGrade();
        switch(currentGrade){
            case BASIC:
                return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            case SILVER:
                return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("Unknown Grade : " + currentGrade);
        }

//        return userGradeUpgradePolicy.canUpgradedGrade(user);
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getGrade().name() + "로 업그레이드 되었습니다.");

        this.mailSender.send(mailMessage);
    }
}
