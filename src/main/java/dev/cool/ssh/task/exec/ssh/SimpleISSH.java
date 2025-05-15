package dev.cool.ssh.task.exec.ssh;

import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.HostInfo;

public class SimpleISSH extends BasicSSH implements ISSH {
    public SimpleISSH(HostInfo hostInfo) {
        super(hostInfo);
    }

    @Override
    public void execute(ExecuteInfo executeInfo) {

    }
}
