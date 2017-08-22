package com.miracles.universalupgrade;

import android.util.Log;

import com.miracles.annotations.UpgradeInstance;
import com.miracles.annotations.VersionUpgrade;
import com.miracles.upgrade.VersionHandler;

/**
 * Created by lxw
 */
@UpgradeInstance(id = "app")
public class AppUpgradeHandler implements VersionHandler {
    String tag = "AppUpgradeHandler";

    @VersionUpgrade(fromVersion = 0, toVersion = 1)
    public void handle01() {
        Log.e(tag, "AppUpgradeHandler handle01 method executed");
    }

    @VersionUpgrade(fromVersion = 1, toVersion = 2)
    public void handle12() {
        Log.e(tag, "AppUpgradeHandler handle12 method executed");

    }

    @Override
    public void setSeedInstance(Object instance) {

    }

    //
    @Override
    public void setVersion(int newVersion) {

    }

    //old=0,new=2 then execute 0-1,and 1-2
    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public int maxVersion() {
        return 2;
    }
}
