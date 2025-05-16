package dev.cool.ssh.task.exec.factory;

import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.exec.ssh.JumpServerSSH;
import dev.cool.ssh.task.exec.ssh.SimpleISSH;
import dev.cool.ssh.task.model.HostInfo;

import java.util.Objects;

public class SSHFactory {
    public static ISSH getSSH(HostInfo hostInfo) {
        if (Objects.equals(hostInfo.getHostType(), 2)) {
            return new JumpServerSSH(hostInfo);
        }
        return new SimpleISSH(hostInfo);
    }
}
