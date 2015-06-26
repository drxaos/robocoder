package com.github.drxaos.robocoder.program;

import java.security.Permission;
import java.util.WeakHashMap;

public class SandboxingSecurityManager extends SecurityManager {

    WeakHashMap<Thread, Boolean> sandboxed = new WeakHashMap<Thread, Boolean>();

    @Override
    public void checkPermission(Permission perm) {
        check(perm);
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        check(perm);
    }

    private void check(Permission perm) {
        System.out.println("check: " + perm);

        Boolean enabled = sandboxed.get(Thread.currentThread());
        if (enabled == null) {
            return;
        }
        if (enabled) {
            throw new SecurityException("Permission denied: " + perm);
        }
    }

    public void sandboxThread(Thread thread) {
        sandboxed.put(thread, true);
    }

    public void disableSandbox(Thread thread) {
        if (sandboxed.containsKey(thread)) {
            sandboxed.put(thread, false);
        }
    }

    public void enableSandbox(Thread thread) {
        if (sandboxed.containsKey(thread)) {
            sandboxed.put(thread, true);
        }
    }
}
