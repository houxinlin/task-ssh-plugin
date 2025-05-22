package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;

public interface ISSH {
    void execute(ExecuteInfoWrapper executeInfo, ExecListener execListener) throws Exception;
}
