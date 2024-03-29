package com.ksh.policy;

import com.ksh.dao.UserDao;
import com.ksh.domain.Grade;
import com.ksh.domain.User;

import static com.ksh.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.ksh.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

public class UserGradeUpgradePolicyAtNormal implements UserGradeUpgradePolicy{
    UserDao userDao;

    public UserGradeUpgradePolicyAtNormal(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean canUpgradedGrade(User user) {
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
    }

    @Override
    public void upgradeGrade(User user) {
        user.upgradeGrade();
        userDao.update(user);
    }
}
