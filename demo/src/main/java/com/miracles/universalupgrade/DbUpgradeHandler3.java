package com.miracles.universalupgrade;

import com.miracles.annotations.UpgradeInstance;
import com.miracles.annotations.VersionUpgrade;

/**
 * Created by lxw
 */
@UpgradeInstance(id = "db")
public class DbUpgradeHandler3 extends BaseDbUpgradeHandler {

    @VersionUpgrade(fromVersion = 1, toVersion = 2)
    public void handle12() {

    }

    @VersionUpgrade(fromVersion = 0, toVersion = 1,priority = 1)
    public void handle01() {

    }


}
