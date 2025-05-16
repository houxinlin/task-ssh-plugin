package dev.cool.ssh.task.exec.factory;

import dev.cool.ssh.task.exec.ITask;
import dev.cool.ssh.task.exec.task.RzFileTransmissionTask;
import dev.cool.ssh.task.model.ExecuteInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TaskFactory {
    public static @NotNull ITask getTask(ExecuteInfo executeInfo) {
        if (Objects.equals(executeInfo.getExecuteType(), 1)) {
            return new RzFileTransmissionTask();
        }
        throw new IllegalArgumentException("无效任务");
    }
}
