package com.miracles.upgrade;

/**
 * Created by lxw
 */
public interface UpgradeManager<T> extends SeedInstance<T> {

    void applyUpgrade() throws UpgradeException;

}
