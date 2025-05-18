package dev.cool.ssh.task.exec.task;

import com.google.gson.Gson;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.SimpleParameter;

import java.util.Objects;

public class KillJarTask extends BasicTask {
    public KillJarTask(ExecuteInfoWrapper executeInfoWrapper) {
        super(executeInfoWrapper);
    }

    @Override
    public void doExecute(ExecContext execContext) throws Exception {
        SimpleParameter simpleParameter = new Gson().fromJson(getExecuteInfoWrapper().getExecuteExtJSON(), SimpleParameter.class);
        if (Objects.nonNull(simpleParameter.getValue())) {
            if (simpleParameter.getValue().isEmpty()) return;
            String command = "jps -l | grep \" " + simpleParameter.getValue() + " \" | awk '{print $1}' | xargs -r kill -9\r";
            execContext.getOutputStream().write(command.getBytes());
            execContext.getOutputStream().flush();
            waitUntilFinished(execContext);
        }
    }
}
