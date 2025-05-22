package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;

public interface ExecListener {
    public void execBegin();

    public void execEnd();

    public void taskExecEnd(ExecuteInfoWrapper info);

    public void execOutput(ExecuteInfoWrapper wrapper, String output);

    public void execTask(ExecuteInfoWrapper wrapper);
}
