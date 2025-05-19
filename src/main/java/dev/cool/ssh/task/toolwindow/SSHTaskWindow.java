package dev.cool.ssh.task.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import dev.cool.ssh.task.view.SSHTaskView;
import org.jetbrains.annotations.NotNull;

public class SSHTaskWindow implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.getContentManager()
                .addContent(toolWindow.getContentManager()
                        .getFactory()
                        .createContent(new JBScrollPane(new SSHTaskView(project)), "", true));
    }
}
