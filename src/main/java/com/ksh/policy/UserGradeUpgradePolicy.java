package com.ksh.policy;

import com.ksh.domain.User;

public interface UserGradeUpgradePolicy {
    boolean canUpgradedGrade(User user);
    void upgradeGrade(User user);
}
