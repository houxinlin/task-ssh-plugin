package dev.cool.ssh.task.model;

public class FileEntry {
    private String fileName;
    private boolean isDirectory;
    private String size;
    private String owner;

    public FileEntry(String fileName, boolean isDirectory, String size, String owner) {
        this.fileName = fileName;
        this.isDirectory = isDirectory;
        this.size = size;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
