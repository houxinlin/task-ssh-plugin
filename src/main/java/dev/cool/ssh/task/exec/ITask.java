package dev.cool.ssh.task.exec;

import com.jcraft.jsch.Channel;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;

public interface ITask {
    public void execute(ExecuteInfoWrapper executeInfo, Channel channel) throws Exception;
}
