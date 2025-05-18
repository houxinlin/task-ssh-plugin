package dev.cool.ssh.task.exec;

import com.intellij.icons.AllIcons;
import com.intellij.ui.AnimatedIcon;
import dev.cool.ssh.task.common.Icons;

import javax.swing.*;

public enum State {
    WAITING(Icons.Timer),
    RUNNING(AnimatedIcon.Default.INSTANCE),
    FAILED(AllIcons.General.Error),
    FINISHED(Icons.Success);
    private Icon icon;

    State(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
