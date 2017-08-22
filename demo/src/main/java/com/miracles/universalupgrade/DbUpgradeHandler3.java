package com.miracles.universalupgrade;

import com.miracles.annotations.UpgradeInstance;
import com.miracles.annotations.VersionUpgrade;

/**
 * Created by lxw
 */
@UpgradeInstance(id = "db")
public class DbUpgradeHandler2 extends BaseDbUpgradeHandler {

    @VersionUpgrade(fromVersion = 2, toVersion = 3)
    public void handle23() {

    }

    @VersionUpgrade(fromVersion = 3, toVersion = 4)
    public void handle34() {

    }


}
