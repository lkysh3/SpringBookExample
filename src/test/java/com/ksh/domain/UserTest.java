package com.ksh.domain;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UserTest {
    User user;

    @Before
    public void setup(){
        user = new User();
    }

    @Test
    public void upgradeGrade(){
        Grade[] grades = Grade.values();

        for (Grade grade : grades) {
            if(grade.nextGrade() == null) continue;
            user.setGrade(grade);
            user.upgradeGrade();
            assertThat(user.getGrade(), is(grade.nextGrade()));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void cannotUpgradeGrade(){
        Grade[] grades = Grade.values();

        for (Grade grade : grades) {
            if(grade.nextGrade() != null) continue;
            user.setGrade(grade);
            user.upgradeGrade();
        }
    }
}
