package dev.cool.ssh.task.utils;

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
}
