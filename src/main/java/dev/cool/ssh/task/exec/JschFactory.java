package dev.cool.ssh.task.exec;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JschFactory {
    public static Channel openChannel(String host, int port, String username, String password) throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        ChannelShell channel = null;
        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
            channel = (ChannelShell) session.openChannel("shell");
            channel.setPty(true);
            channel.connect();

        } catch (Exception e) {
            throw e;
        }
        return channel;
    }
}
