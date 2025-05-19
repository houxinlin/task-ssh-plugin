package dev.cool.ssh.task.exec;

import com.jcraft.jsch.*;
import dev.cool.ssh.task.model.HostInfo;

public class JschFactory {
    public static ChannelShell openChannel(HostInfo hostInfo) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(hostInfo.getUsername(), hostInfo.getHost(), hostInfo.getPort());
        session.setPassword(hostInfo.getPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelShell channel = (ChannelShell) session.openChannel("shell");
        channel.setPty(true);
        return channel;
    }

    public static ChannelExec openExecChannel(HostInfo hostInfo) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(hostInfo.getUsername(), hostInfo.getHost(), hostInfo.getPort());
        session.setPassword(hostInfo.getPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        return channel;
    }

    public static ChannelSftp openSFTP(HostInfo hostInfo) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(hostInfo.getUsername(), hostInfo.getHost(), hostInfo.getPort());
        session.setPassword(hostInfo.getPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }
}
