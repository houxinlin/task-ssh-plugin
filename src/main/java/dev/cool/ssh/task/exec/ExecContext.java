package dev.cool.ssh.task.exec;

import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.HostInfo;

import java.io.InputStream;
import java.io.OutputStream;

public class ExecContext {
    private String sshPrompt;
    private InputStream inputStream;
    private OutputStream outputStream;
    private HostInfo hostInfo;
    private ExecuteInfoWrapper executeInfoWrapper;
    public ExecContext(String sshPrompt, InputStream inputStream, OutputStream outputStream) {
        this.sshPrompt = sshPrompt;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public ExecuteInfoWrapper getExecuteInfoWrapper() {
        return executeInfoWrapper;
    }

    public void setExecuteInfoWrapper(ExecuteInfoWrapper executeInfoWrapper) {
        this.executeInfoWrapper = executeInfoWrapper;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    public String getSshPrompt() {
        return sshPrompt;
    }

    public void setSshPrompt(String sshPrompt) {
        this.sshPrompt = sshPrompt;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
