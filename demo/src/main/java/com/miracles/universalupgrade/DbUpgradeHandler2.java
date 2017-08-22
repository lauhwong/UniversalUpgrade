package com.miracles.universalupgrade;

import android.database.sqlite.SQLiteDatabase;

import com.miracles.annotations.UpgradeInstance;
import com.miracles.annotations.VersionUpgrade;
import com.miracles.upgrade.VersionHandler;

/**
 * Created by lxw
 */
@UpgradeInstance(id = "db")
public class DbUpgradeHandler implements VersionHandler<SQLiteDatabase> {

    @VersionUpgrade(fromVersion = 0, toVersion = 1)
    public void handle01() {

    }

    @VersionUpgrade(fromVersion = 4, toVersion = 5)
    public void handle45() {

    }

    @Override
    public void setSeedInstance(SQLiteDatabase instance) {

    }

    @Override
    public void setVersion(int newVersion) {
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public int maxVersion() {
        return 0;
    }

}
