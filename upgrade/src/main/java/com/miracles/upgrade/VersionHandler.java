package com.miracles.upgrade;

/**
 * Created by lxw
 */
public interface VersionHandler<T> extends SeedInstance<T> {

    void setVersion(int newVersion);

    int getVersion();

    int maxVersion();

}
