package dev.cool.ssh.task.exec.task;

import com.jcraft.jsch.Channel;
import dev.cool.ssh.task.exec.ITask;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;

public abstract class BasicTask implements ITask {
    public abstract void doExecute(ExecuteInfoWrapper executeInfo, Channel channel) throws Exception;

    @Override
    public void execute(ExecuteInfoWrapper executeInfo, Channel channel) throws Exception {
        doExecute(executeInfo, channel);
    }
}
