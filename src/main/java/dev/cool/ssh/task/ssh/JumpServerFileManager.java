package dev.cool.ssh.task.ssh;

import com.intellij.openapi.application.ApplicationManager;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import dev.cool.ssh.task.exec.JschFactory;
import dev.cool.ssh.task.model.FileEntry;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.JumpServerHostInfo;
import dev.cool.ssh.task.utils.ExecUtils;
import dev.cool.ssh.task.utils.JSONUtils;
import dev.cool.ssh.task.utils.LSParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class JumpServerFileManager implements FileSystemManager {
    private final HostInfo hostInfo;
    private volatile InputStream inputStream;
    private volatile OutputStream outputStream;
    private volatile ChannelShell channelShell;
    private volatile String prompt;

    private ConnectionListener connectionListener;

    public JumpServerFileManager(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    @Override
    public void connectFileSystem(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
        ApplicationManager.getApplication().executeOnPooledThread(this::doConnection);
    }

    private void doConnection() {
        try {
            channelShell = JschFactory.openChannel(hostInfo);
            inputStream = channelShell.getInputStream();
            outputStream = channelShell.getOutputStream();
            JumpServerConnection jumpServerConnection = new JumpServerConnection();
            prompt = jumpServerConnection.tryConnectAndWait(
                    inputStream,
                    outputStream,
                    JSONUtils.fromJSON(hostInfo.getHostExtJSON(), JumpServerHostInfo.class));

            if (prompt != null && !prompt.isEmpty()) {
                this.connectionListener.connectionSuccess();
            }
        } catch (Exception ignored) {
            this.connectionListener.connectionFailed();
        }

    }

    @Override
    public void dispose() {
        ExecUtils.closeChannel(channelShell);
    }

    private ByteArrayOutputStream doListFile(String path) {
        if (outputStream != null) {
            try {
                outputStream.write(("ls --color=never -lh " + path + "  \r").getBytes());
                outputStream.flush();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                while (true) {
                    int read = inputStream.read(buffer);
                    if (read == -1) {
                        return result;
                    }
                    result.write(buffer, 0, read);

                    String[] lines = result.toString().split("\\R");
                    String lastLine = lines.length > 0 ? lines[lines.length - 1] : "";
                    if (lastLine.contains(prompt)) return result;
                }

            } catch (IOException ignored) {

            }
        }
        return null;
    }

    public List<FileEntry> listFile(String path) {
        ByteArrayOutputStream byteArrayOutputStream = doListFile(path);
        if (byteArrayOutputStream != null) {
            return LSParser.parseLsOutput(byteArrayOutputStream.toString());
        }
        return null;
    }


}
