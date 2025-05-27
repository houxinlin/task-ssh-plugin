package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.exec.factory.SSHFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.exec.wrapper.HostInfoWrapper;
import dev.cool.ssh.task.view.node.ExecutionNode;

import java.util.List;

public class TaskExec implements Runnable {
    private final List<HostInfoWrapper> task;
    private final ExecListener execListener;

    public TaskExec(List<HostInfoWrapper> task, ExecListener execListener) {
        this.task = task;
        this.execListener = execListener;
    }

    public List<HostInfoWrapper> getTask() {
        return task;
    }

    private void execTask(HostInfoWrapper hostInfo) {
        execListener.execBegin();
        for (ExecuteInfoWrapper executeInfo : hostInfo.getExecuteInfos()) {
            ISSH ssh = SSHFactory.getSSH(hostInfo.getHostInfo());
            execListener.execTask(executeInfo);
            for (int i = 0; i < 3; i++) {
                if (executeInfo.getNode().isCancelled()) break;
                ExecutionNode executeInfoNode = executeInfo.getNode();
                executeInfoNode.setTryCount(i);
                try {
                    executeInfoNode.setState(State.RUNNING);
                    ssh.execute(executeInfo, execListener);
                    executeInfoNode.setState(State.FINISHED);
                    break;
                } catch (Exception e) {
                    execListener.execOutput(executeInfo, e.getMessage());
                    executeInfoNode.setState(State.FAILED);
                    executeInfoNode.setErrorMessage(e.getMessage());
                }
            }
            execListener.taskExecEnd(executeInfo);
        }
        execListener.execEnd();
    }

    @Override
    public void run() {
        for (HostInfoWrapper host : task) {
            execTask(host);
        }
    }
}
