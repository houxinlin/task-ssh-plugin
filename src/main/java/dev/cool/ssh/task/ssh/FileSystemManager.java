package dev.cool.ssh.task.ssh;

import com.intellij.openapi.Disposable;
import dev.cool.ssh.task.model.FileEntry;

import java.util.List;

public interface FileSystemManager extends Disposable {
    public void connectFileSystem(ConnectionListener connectionListener);

    public List<FileEntry> listFile(String path);
}
