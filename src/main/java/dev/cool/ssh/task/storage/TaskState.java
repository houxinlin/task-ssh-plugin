package dev.cool.ssh.task.storage;

import dev.cool.ssh.task.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskState {
    private List<Task> taskList = new ArrayList<>();

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
