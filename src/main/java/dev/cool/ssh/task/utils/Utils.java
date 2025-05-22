package dev.cool.ssh.task.utils;

import com.intellij.openapi.project.Project;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.view.dialog.LinuxDirectoryChooseDialog;

public class Utils {
    public static String getLinuxParentPath(String linuxPath) {
        if (linuxPath == null || linuxPath.isEmpty()) {
            return null;
        }
        String path = linuxPath.endsWith("/") && linuxPath.length() > 1
                ? linuxPath.substring(0, linuxPath.length() - 1)
                : linuxPath;

        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex <= 0) {
            return null;
        }
        return path.substring(0, lastSlashIndex);
    }

    public static String getPath(boolean directory, Project project, HostInfo hostInfo) {
        LinuxDirectoryChooseDialog linuxDirectoryChooseDialog = new LinuxDirectoryChooseDialog(directory, project, hostInfo);
        linuxDirectoryChooseDialog.show();
        if (linuxDirectoryChooseDialog.isOK()) {
            return linuxDirectoryChooseDialog.getSelectedDirectory();
        }
        return null;
    }
}
