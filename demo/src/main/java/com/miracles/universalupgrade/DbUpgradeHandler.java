package com.miracles.universalupgrade;

import com.miracles.annotations.UpgradeInstance;
import com.miracles.annotations.VersionUpgrade;

/**
 * Created by lxw
 */
@UpgradeInstance(id = "db")
public class DbUpgradeHandler extends BaseDbUpgradeHandler {

    @VersionUpgrade(fromVersion = 0, toVersion = 1)
    public void handle01() {

    }

    @VersionUpgrade(fromVersion = 4, toVersion = 5)
    public void handle45() {

    }


}
