package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.exec.factory.SSHFactory;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.Task;

public class TaskExec implements Runnable {
    private final Task task;

    public TaskExec(Task task) {
        this.task = task;
    }

    private void execTask(HostInfo hostInfo) {
        for (ExecuteInfo executeInfo : task.getExecutes()) {
            ISSH ISSH = SSHFactory.getSSH(hostInfo);
            ISSH.execute(executeInfo);
        }
    }

    @Override
    public void run() {
        for (HostInfo host : task.getHosts()) {
            execTask(host);
        }
    }
}
