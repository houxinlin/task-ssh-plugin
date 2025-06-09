package dev.cool.ssh.task.exec.task;

import com.google.gson.Gson;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.CommandParameter;

public class CommandTask extends BasicTask {
    public CommandTask(ExecuteInfoWrapper executeInfoWrapper) {
        super(executeInfoWrapper);
    }

    @Override
    public void doExecute(ExecContext execContext) throws Exception {
        CommandParameter parameter = new Gson().fromJson(getExecuteInfoWrapper().getExecuteExtJSON(), CommandParameter.class);
        if (parameter.getValue() == null || parameter.getValue().isEmpty()) return;

        String command = CommandBuilder.buildCommand(execContext.getExecuteInfoWrapper());
        execContext.getOutputStream().write((command + "\r").getBytes());
        execContext.getOutputStream().flush();
        waitUntilFinished(execContext);
    }
}
