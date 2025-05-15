package dev.cool.ssh.task.view;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import dev.cool.ssh.task.model.Task;
import dev.cool.ssh.task.storage.TaskStorage;
import org.jetbrains.annotations.NotNull;

public class SSHTaskView extends SimpleToolWindowPanel {

    public SSHTaskView() {
        super(true);
        initView();
    }

    private void initView() {
        SSHTaskContent sshTaskContent = new SSHTaskContent();
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.add(new AnAction("add", "add", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                TaskStorage.getInstance().getTasks().add(new Task());
                sshTaskContent.reload();
            }
        });
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("toolbar@MainToolWindows", defaultActionGroup, true);
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
        setContent(sshTaskContent);

    }
}
