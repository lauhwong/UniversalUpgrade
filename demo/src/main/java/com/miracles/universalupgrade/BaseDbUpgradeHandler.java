package com.miracles.universalupgrade;

import android.database.sqlite.SQLiteDatabase;

import com.miracles.upgrade.VersionHandler;

/**
 * Created by lxw
 */
public class BaseDbUpgradeHandler implements VersionHandler<SQLiteDatabase> {
    protected SQLiteDatabase instance;

    @Override
    public void setSeedInstance(SQLiteDatabase instance) {
        this.instance = instance;
    }

    @Override
    public void setVersion(int newVersion) {
        instance.setVersion(newVersion);
    }

    @Override
    public int getVersion() {
        return instance.getVersion();
    }

    @Override
    public int maxVersion() {
        return 5;
    }
}
