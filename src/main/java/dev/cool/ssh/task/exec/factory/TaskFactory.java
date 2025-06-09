package dev.cool.ssh.task.exec.factory;

import dev.cool.ssh.task.exec.ExecType;
import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.exec.ITask;
import dev.cool.ssh.task.exec.ssh.JumpServerSSH;
import dev.cool.ssh.task.exec.ssh.SimpleISSH;
import dev.cool.ssh.task.exec.task.*;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TaskFactory {
    public static @NotNull ITask getTask(ExecuteInfoWrapper executeInfo, ISSH issh) {
        if (issh instanceof JumpServerSSH) {
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.UPLOAD.getExecType())) {
                return new RzFileTransmissionTask(executeInfo);
            }
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.KILL_JAR.getExecType())) {
                return new KillJarTask(executeInfo);
            }
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.COMMAND.getExecType()) ||
                    Objects.equals(executeInfo.getExecuteType(), ExecType.KILL_PORT.getExecType())) {
                return new CommandTask(executeInfo);
            }
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.SCRIPT.getExecType())) {
                return new ScriptExecuteTask(executeInfo);
            }
        }
        if (issh instanceof SimpleISSH) {
            return SimpleTaskFactory.getTask(executeInfo);
        }
        throw new IllegalArgumentException("无效任务");
    }
}
