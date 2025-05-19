package dev.cool.ssh.task.exec.task;

import com.google.gson.Gson;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.SimpleParameter;
import dev.cool.ssh.task.utils.ExecUtils;

public class ScriptExecuteTask extends BasicTask {
    @Override
    public void doExecute(ExecContext execContext) throws Exception {
        SimpleParameter simpleParameter = new Gson().fromJson(getExecuteInfoWrapper().getExecuteExtJSON(), SimpleParameter.class);
        if (simpleParameter.getValue() == null || simpleParameter.getValue().isEmpty()) return;

        String command = ExecUtils.buildExecCmd(execContext.getExecuteInfoWrapper().getExecuteInfo());
        execContext.getOutputStream().write((command + "\r").getBytes());
        execContext.getOutputStream().flush();
        waitUntilFinished(execContext);
    }

    public ScriptExecuteTask(ExecuteInfoWrapper executeInfoWrapper) {
        super(executeInfoWrapper);
    }
}
