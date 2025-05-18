package dev.cool.ssh.task.exec.ssh;

import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.exec.SimpleExecContext;
import dev.cool.ssh.task.exec.factory.TaskFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.HostInfo;

public class SimpleISSH extends BasicSSH implements ISSH {
    public SimpleISSH(HostInfo hostInfo) {
        super(hostInfo);
    }

    @Override
    public void execute(ExecuteInfoWrapper executeInfo) {
        try {
            SimpleExecContext simpleExecContext = new SimpleExecContext(getHostInfo());
            simpleExecContext.setExecuteInfoWrapper(executeInfo);
            TaskFactory.getTask(executeInfo, this).execute(simpleExecContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
