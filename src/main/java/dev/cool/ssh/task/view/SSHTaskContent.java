package dev.cool.ssh.task.view;

import com.intellij.openapi.ui.VerticalFlowLayout;
import dev.cool.ssh.task.model.Task;
import dev.cool.ssh.task.storage.TaskStorage;

import javax.swing.*;

public class SSHTaskContent  extends JPanel {
    public SSHTaskContent() {
        setLayout(new VerticalFlowLayout());
        reload();
    }

    public void reload() {
        removeAll();
        for (Task task : TaskStorage.getInstance().getTasks()) {
            add(new SSHTaskItem(task));
        }
    }
}
