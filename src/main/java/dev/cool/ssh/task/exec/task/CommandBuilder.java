package dev.cool.ssh.task.exec.task;

import com.google.gson.Gson;
import dev.cool.ssh.task.exec.ExecType;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.CommandParameter;
import dev.cool.ssh.task.model.KillPortParameter;

import java.util.Objects;

public class CommandBuilder {
    public static String buildCommand(ExecuteInfoWrapper executeInfoWrapper) {
        if (Objects.equals(executeInfoWrapper.getExecuteType(), ExecType.COMMAND.getExecType())) {
            CommandParameter simpleParameter = new Gson().fromJson(executeInfoWrapper.getExecuteExtJSON(), CommandParameter.class);
            if (simpleParameter.getValue() == null || simpleParameter.getValue().isEmpty()) return null;
            if (simpleParameter.getDirectory() != null && !simpleParameter.getDirectory().isEmpty()) {
                return "cd " + simpleParameter.getDirectory() + " && " + simpleParameter.getValue();
            } else {
                return simpleParameter.getValue();
            }
        }
        if (Objects.equals(executeInfoWrapper.getExecuteType(), ExecType.KILL_PORT.getExecType())) {
            KillPortParameter simpleParameter = new Gson().fromJson(executeInfoWrapper.getExecuteExtJSON(), KillPortParameter.class);
            if (simpleParameter.getValue() == null || simpleParameter.getValue().isEmpty()) return null;
            if (simpleParameter.isSudo())
                return "sudo kill -9 $(sudo lsof -t -i:%s)".formatted(simpleParameter.getValue());
            return "kill -9 $(lsof -t -i:%s)".formatted(simpleParameter.getValue());
        }
        return null;
    }
}
