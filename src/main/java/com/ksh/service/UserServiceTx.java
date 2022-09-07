package com.ksh.service;

import com.ksh.domain.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserServiceTx implements UserService{
    UserService userService;
    PlatformTransactionManager transactionManager;

    public void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionManager = transactionManager;
    }

    // 작업을 위임할 오브젝트를 DI 받는다.
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    // 위임받은 오브젝트에게 모든 작업 위임
    @Override
    public void add(User user) {
        userService.add(user);
    }

    @Override
    public User get(String id) {
        return userService.get(id);
    }

    @Override
    public List<User> getAll() {
        return userService.getAll();
    }

    @Override
    public void deleteAll() {
        userService.deleteAll();
    }

    @Override
    public void update(User user) {
        userService.update(user);
    }

    @Override
    public void upgradeGrades() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try{
            userService.upgradeGrades();

            this.transactionManager.commit(status);
        }catch(RuntimeException e){
            this.transactionManager.rollback(status);
        }
    }
}
