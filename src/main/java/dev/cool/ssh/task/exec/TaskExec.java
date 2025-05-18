package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.exec.factory.SSHFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.exec.wrapper.HostInfoWrapper;
import dev.cool.ssh.task.view.node.ExecutionNode;
import dev.cool.ssh.task.view.node.TimerExecuteNode;

import java.util.List;

public class TaskExec implements Runnable {
    private final List<HostInfoWrapper> task;

    public TaskExec(List<HostInfoWrapper> task) {
        this.task = task;
    }

    private void execTask(HostInfoWrapper hostInfo) {
        for (ExecuteInfoWrapper executeInfo : hostInfo.getExecuteInfos()) {
            ISSH ssh = SSHFactory.getSSH(hostInfo.getHostInfo());
            for (int i = 0; i < 3; i++) {
                ExecutionNode executeInfoNode = executeInfo.getNode();
                try {
                    executeInfoNode.setTryCount(i);
                    executeInfoNode.setState(State.RUNNING);
                    ssh.execute(executeInfo);
                    executeInfoNode.setState(State.FINISHED);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    executeInfoNode.setState(State.FAILED);
                }
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
