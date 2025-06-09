package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.HostInfo;
import org.jetbrains.annotations.Nullable;

public abstract class ExecParameterDialogWrapper extends DialogWrapper {
    private ExecuteInfo executeInfo;
    private HostInfo hostInfo;

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
            executeInfo.setExecuteExtJSON(buildExtJSON());
        }
    }

    public void setExecuteInfo(ExecuteInfo executeInfo) {
        this.executeInfo = executeInfo;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    public ExecuteInfo getExecuteInfo() {
        return executeInfo;
    }
}
