package com.miracles.universalupgrade;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miracles.support.upgrade.DbUpgradeManager;
import com.miracles.upgrade.UpgradeException;

/**
 * Created by lxw
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DbUpgradeManager dbUpgradeManager = new DbUpgradeManager();
        dbUpgradeManager.setSeedInstance(db);
        try {
            dbUpgradeManager.applyUpgrade();
        } catch (UpgradeException e) {
            e.printStackTrace();
        }
    }
}
