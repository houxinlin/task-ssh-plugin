package dev.cool.ssh.task.exec.ssh;

import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.exec.ITask;
import dev.cool.ssh.task.exec.factory.TaskFactory;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.HostInfo;

public class JumpServerISSH extends BasicSSH implements ISSH {
    public JumpServerISSH(HostInfo hostInfo) {
        super(hostInfo);
    }

    @Override
    public void execute(ExecuteInfo executeInfo) {
        ITask task = TaskFactory.getTask(executeInfo);

    }
}
