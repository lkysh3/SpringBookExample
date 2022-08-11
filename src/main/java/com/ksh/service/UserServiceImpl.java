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

public class UserServiceImpl {
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
        // DataSourceTransactionManager ->  JdbcTemplete에서 사용될 수 있는 방식으로 트랜잭션을 관리해준다.
        //PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // 트랜잭션 매니저가 필요에 따라 DB 커넥션도 같이 가져온다.
        // 트랜잭션 시작
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            upgradeGradesInternal();

            this.transactionManager.commit(status);
        }catch(Exception e){
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    private void upgradeGradesInternal() {
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

//        Properties props = new Properties();
//        props.put("mail.smtp.host", "mail.ksug.org");
//        Session s = Session.getInstance(props, null);
//        MimeMessage message = new MimeMessage(s);
//
//        try{
//            message.setFrom(new InternetAddress("useradmin@ksug.org"));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
//            message.setSubject("Upgrade 안내");
//            message.setText("사용자님의 등급이 " + user.getGrade().name() + "로 업그레이드 되었습니다.");
//
//            Transport.send(message);
//        }catch(AddressException e){
//            throw new RuntimeException(e);
//        }catch(MessagingException e){
//            throw new RuntimeException(e);
//        }
    }
}
