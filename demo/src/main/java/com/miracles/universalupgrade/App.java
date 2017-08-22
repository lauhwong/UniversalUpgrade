package com.miracles.universalupgrade;

import android.app.Application;

import com.miracles.support.upgrade.AppUpgradeManager;
import com.miracles.upgrade.UpgradeException;

/**
 * Created by lxw
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppUpgradeManager appUpgradeManager = new AppUpgradeManager();
        try {
            appUpgradeManager.applyUpgrade();
        } catch (UpgradeException e) {
            e.printStackTrace();
        }
    }
}
