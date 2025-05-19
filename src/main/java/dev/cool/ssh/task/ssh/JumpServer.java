package dev.cool.ssh.task.ssh;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import dev.cool.ssh.task.exec.JschFactory;
import dev.cool.ssh.task.model.HostInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JumpServer implements Disposable {
    private final HostInfo hostInfo;

    private ChannelShell channelShell;

    public JumpServer(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }
    public void beginListHost(JumpServerHostFinder jumpServerHostFinder) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> doBeginListHost(jumpServerHostFinder));
    }

    @Override
    public void dispose() {
        closeChannel();
    }

    private void doBeginListHost(JumpServerHostFinder jumpServerHostFinder) {
        try {
            channelShell = JschFactory.openChannel(hostInfo);
            OutputStream outputStream = channelShell.getOutputStream();
            InputStream inputStream = channelShell.getInputStream();
            channelShell.connect();
            outputStream.write("p\r".getBytes());
            outputStream.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            Pattern pattern = Pattern.compile("\\s*(\\d+)\\s*\\|\\s*([^|]+?)\\s*\\|\\s*([\\d.]+)\\s*\\|\\s*(.*?)\\s*$");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String id = matcher.group(1);
                    String hostname = matcher.group(2);
                    String ip = matcher.group(3);
                    jumpServerHostFinder.findJumpServerHost(id, hostname, ip, "");
                }
            }

        } catch (Exception ignored) {
        }
    }

    public void closeChannel() {
        if (channelShell != null) {
            channelShell.disconnect();
            try {
                channelShell.getSession().disconnect();
            } catch (JSchException ignored) {

            }
        }
    }
}