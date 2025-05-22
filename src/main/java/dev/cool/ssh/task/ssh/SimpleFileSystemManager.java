package dev.cool.ssh.task.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import dev.cool.ssh.task.exec.JschFactory;
import dev.cool.ssh.task.model.FileEntry;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.utils.ExecUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SimpleFileSystemManager implements FileSystemManager {
    private HostInfo hostInfo;

    private ChannelSftp channelSftp;

    public SimpleFileSystemManager(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    @Override
    public void connectFileSystem(ConnectionListener connectionListener) {
        try {
            channelSftp = JschFactory.openSFTP(hostInfo);
            connectionListener.connectionSuccess();
        } catch (Exception e) {
            connectionListener.connectionFailed();
        }
    }

    @Override
    public List<FileEntry> listFile(String path) {
        List<FileEntry> result = new ArrayList<>();
        try {
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(path);
            for (ChannelSftp.LsEntry entry : files) {
                if (!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                    result.add(new FileEntry(entry.getFilename(), entry.getAttrs().isDir(), entry.getAttrs().getSize() + "", ""));
                }
            }
        } catch (SftpException ignored) {
        }
        return result;
    }

    @Override
    public void dispose() {
        ExecUtils.closeChannel(channelSftp);
    }
}
