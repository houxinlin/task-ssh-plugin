package dev.cool.ssh.task.model;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.jcraft.jsch.ChannelShell;
import dev.cool.ssh.task.exec.JschFactory;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class HostInfo {
    private String host;
    private int port;
    private String username;
    private String password;
    private int hostType;
    private String hostExtJSON;
    private int sort;
    private List<ExecuteInfo> executeInfos;

    public void testConnection(Project project, ConnectionTestCallback callback) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                ChannelShell channel = JschFactory.openChannel(this);
                channel.disconnect();
                SwingUtilities.invokeLater(() -> {
                    Messages.showInfoMessage(project, "连接成功！", "成功");
                    callback.onSuccess();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    Messages.showErrorDialog(project, "连接失败：" + e.getMessage(), "错误");
                    callback.onError(e.getMessage());
                });
            }
        });
    }

    public List<ExecuteInfo> getExecuteInfos() {
        if (executeInfos == null) executeInfos = new ArrayList<>();
        return executeInfos;
    }
    public void setExecuteInfos(List<ExecuteInfo> executeInfos) {
        this.executeInfos = executeInfos;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHostType() {
        return hostType;
    }

    public void setHostType(int hostType) {
        this.hostType = hostType;
    }

    public String getHostExtJSON() {
        return hostExtJSON;
    }

    public void setHostExtJSON(String hostExtJSON) {
        this.hostExtJSON = hostExtJSON;
    }
}
