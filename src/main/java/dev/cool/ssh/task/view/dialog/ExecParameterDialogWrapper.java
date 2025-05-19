package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import dev.cool.ssh.task.model.ExecuteInfo;
import org.jetbrains.annotations.Nullable;

public abstract class ExecParameterDialogWrapper extends DialogWrapper {
    private ExecuteInfo executeInfo;

    public ExecParameterDialogWrapper(@Nullable Project project) {
        this(project, null);
    }

    public ExecParameterDialogWrapper(@Nullable Project project, ExecuteInfo executeInfo) {
        super(project);
        this.executeInfo = executeInfo;
    }

    protected abstract String buildExtJSON();

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (executeInfo != null) {
            ApplicationManager.getApplication().executeOnPooledThread(() -> executeInfo.setExecuteExtJSON(buildExtJSON()));
        }
    }

    public ExecuteInfo getExecuteInfo() {
        return executeInfo;
    }
}
