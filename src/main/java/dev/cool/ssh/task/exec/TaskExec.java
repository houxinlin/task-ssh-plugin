package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.exec.factory.SSHFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.exec.wrapper.HostInfoWrapper;
import dev.cool.ssh.task.view.SSHTaskItem;
import dev.cool.ssh.task.view.node.ExecutionNode;

import java.util.List;

public class TaskExec implements Runnable {
    private final List<HostInfoWrapper> task;

    public TaskExec(List<HostInfoWrapper> task) {
        this.task = task;
    }

    private void execTask(HostInfoWrapper hostInfo) {
        for (ExecuteInfoWrapper executeInfo : hostInfo.getExecuteInfos()) {
            ISSH ssh = SSHFactory.getSSH(hostInfo);
            try {
                ((ExecutionNode) executeInfo.getNode()).setState(State.RUNNING);
                ssh.execute(executeInfo);
                ((ExecutionNode) executeInfo.getNode()).setState(State.FINISHED);
            } catch (Exception e) {
                e.printStackTrace();
                ((ExecutionNode) executeInfo.getNode()).setState(State.FAILED);
            }
        }
    }

    @Override
    public void run() {
        for (HostInfoWrapper host : task) {
            execTask(host);
        }
    }
}
