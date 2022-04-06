package com.ksh.service;

import com.ksh.dao.UserDao;
import com.ksh.domain.Grade;
import com.ksh.domain.User;
import com.ksh.policy.UserGradeUpgradePolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;
    UserGradeUpgradePolicy userGradeUpgradePolicy;

    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    public void setUserGradeUpgradePolicy(UserGradeUpgradePolicy userGradeUpgradePolicy){
        this.userGradeUpgradePolicy = userGradeUpgradePolicy;
    }

    public void upgradeGrades(){
        List<User> users = userDao.getAll();

        for (User user : users) {
            if(canUpgradeLevel(user)){
                upgradeGrade(user);
            }
        }
    }

    public void upgradeGrade(User user){
//        user.upgradeGrade();
//        userDao.update(user);

        userGradeUpgradePolicy.upgradeGrade(user);
    }

    public void add(User user) {
        if(user.getGrade() == null){
            user.setGrade(Grade.BASIC);
        }

        userDao.add(user);
    }

    private boolean canUpgradeLevel(User user){
//        Grade currentGrade = user.getGrade();
//        switch(currentGrade){
//            case BASIC:
//                return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
//            case SILVER:
//                return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
//            case GOLD:
//                return false;
//            default:
//                throw new IllegalArgumentException("Unknown Grade : " + currentGrade);
//        }

        return userGradeUpgradePolicy.canUpgradedGrade(user);
    }
}
