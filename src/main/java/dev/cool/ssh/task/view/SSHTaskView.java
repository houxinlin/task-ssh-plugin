package dev.cool.ssh.task.view;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import dev.cool.ssh.task.model.Task;
import dev.cool.ssh.task.storage.TaskStorage;
import dev.cool.ssh.task.utils.JSONUtils;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notification;

public class SSHTaskView extends SimpleToolWindowPanel {
    private Project project;

    public SSHTaskView(Project project) {
        super(true);
        this.project = project;
        initView();
    }

    private void initView() {
        SSHTaskContent sshTaskContent = new SSHTaskContent(project);
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.add(new AnAction("Add", "Add", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                TaskStorage.getInstance().getTasks().add(new Task());
                sshTaskContent.reload();
            }
        });
        defaultActionGroup.add(new AnAction("Export", "Export", AllIcons.General.Export) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                descriptor.setTitle("Export");
                VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
                if (file != null) {
                    Path path = Paths.get(file.getPath(), "ssh-task-export.json");
                    List<Task> tasks = TaskStorage.getInstance().getTasks();
                    String json = JSONUtils.toJSON(tasks);
                    try {
                        Files.write(path, json.getBytes());
                        NotificationGroupManager.getInstance()
                                .getNotificationGroup("SSH Task Plugin")
                                .createNotification("Export success", "Tasks exported successfully to: " + path, NotificationType.INFORMATION)
                                .notify(project);
                    } catch (IOException e) {
                        NotificationGroupManager.getInstance()
                                .getNotificationGroup("SSH Task Plugin")
                                .createNotification("Export failed", "Export failed: " + e.getMessage(), NotificationType.ERROR)
                                .notify(project);
                    }
                }
            }
        });
        defaultActionGroup.add(new AnAction("Import", "Import", AllIcons.ToolbarDecorator.Import) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("json");
                descriptor.setTitle("Import");
                VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
                if (file != null) {
                    try {
                        String json = new String(file.contentsToByteArray());
                        Task[] tasks = JSONUtils.fromJSON(json, Task[].class);
                        TaskStorage.getInstance().getTasks().addAll(java.util.Arrays.asList(tasks));
                        sshTaskContent.reload();
                        NotificationGroupManager.getInstance()
                                .getNotificationGroup("SSH Task Plugin")
                                .createNotification("Import success", "Successfully imported " + tasks.length + " tasks", NotificationType.INFORMATION)
                                .notify(project);
                    } catch (Exception e) {
                        NotificationGroupManager.getInstance()
                                .getNotificationGroup("SSH Task Plugin")
                                .createNotification("Import failed", "Import failed: " + e.getMessage(), NotificationType.ERROR)
                                .notify(project);
                    }
                }
            }
        });
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("toolbar@MainToolWindows", defaultActionGroup, true);
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
        setContent(sshTaskContent);

    }
}
