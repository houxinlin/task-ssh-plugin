package dev.cool.ssh.task.storage;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import dev.cool.ssh.task.model.Task;

import java.util.List;

@State(
        name = "CoolTaskSSHSettings",
        storages = @Storage("CoolTaskSSHSettings.xml")
)
@Service()
public final class TaskStorage implements PersistentStateComponent<TaskState> {
    private TaskState state = new TaskState();

    public static TaskStorage getInstance() {
        return ApplicationManager.getApplication().getService(TaskStorage.class);
    }

    @Override
    public TaskState getState() {
        return state;
    }

    @Override
    public void loadState(TaskState state) {
        this.state = state;
    }

    public List<Task> getTasks() {
        return state.getTaskList();
    }

    public void setTasks(List<Task> tasks) {
        state.setTaskList(tasks);
    }
}
