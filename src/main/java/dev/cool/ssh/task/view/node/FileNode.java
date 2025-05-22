package dev.cool.ssh.task.view.node;

import dev.cool.ssh.task.model.FileEntry;

import javax.swing.tree.DefaultMutableTreeNode;

public class FileNode extends DefaultMutableTreeNode {
    private final FileEntry fileEntry;
    private boolean finish;

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public FileNode(FileEntry userObject) {
        super(userObject);
        this.fileEntry = userObject;
    }

    @Override
    public boolean isLeaf() {
        return !this.fileEntry.isDirectory();
    }

    public FileEntry getFileEntry() {
        return fileEntry;
    }
}
