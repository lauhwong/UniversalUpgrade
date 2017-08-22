package com.miracles.upgrade;

/**
 * Created by lxw
 */
public class UpgradeException extends Exception {

    public UpgradeException(String detailMessage, int fromVersion, int toVersion) {
        super("【executing data upgrade exception:fromVersion( " + fromVersion + " )--->toVersion( " + toVersion + " )】" + detailMessage);
    }

    public UpgradeException(String detailMessage) {
        super(detailMessage);
    }

    public UpgradeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UpgradeException(String detailMessage, Throwable throwable, int fromVersion, int toVersion) {
        super("【executing data upgrade exception:fromVersion( " + fromVersion + " )--->toVersion( " + toVersion + " )】" + detailMessage, throwable);
    }

    public UpgradeException(Throwable throwable) {
        super(throwable);
    }
}
