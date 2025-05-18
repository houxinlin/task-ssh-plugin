package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.model.HostInfo;

public class SimpleExecContext extends ExecContext {
    public SimpleExecContext(HostInfo hostInfo) {
        super(null, null, null);
        setHostInfo(hostInfo);
    }
}
