package dev.cool.ssh.task.exec.task;

import com.google.gson.Gson;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.SimpleParameter;

public class ExecuteCommandTask extends BasicTask {
    public ExecuteCommandTask(ExecuteInfoWrapper executeInfoWrapper) {
        super(executeInfoWrapper);
    }

    @Override
    public void doExecute(ExecContext execContext) throws Exception {
        SimpleParameter simpleParameter = new Gson().fromJson(getExecuteInfoWrapper().getExecuteExtJSON(), SimpleParameter.class);
        if (simpleParameter.getValue() == null || simpleParameter.getValue().isEmpty()) return;
        execContext.getOutputStream().write((simpleParameter.getValue() + "\r").getBytes());
        execContext.getOutputStream().flush();
        waitUntilFinished(execContext);
    }
}
