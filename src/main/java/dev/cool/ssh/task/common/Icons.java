package dev.cool.ssh.task.common;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public interface Icons {
    Icon Timer = IconLoader.getIcon("/icons/startTimer.svg", Icons.class);
    Icon Success = IconLoader.getIcon("/icons/success.svg", Icons.class);
    Icon TaskGroup = IconLoader.getIcon("/icons/assets.svg", Icons.class);
    Icon JumpServer = IconLoader.getIcon("/icons/jumpserver.svg", Icons.class);
    Icon Host = IconLoader.getIcon("/icons/linux.svg", Icons.class);
    @Nullable Icon Delete = IconLoader.getIcon("/icons/delete.svg", Icons.class);
    @Nullable Icon Shell = IconLoader.getIcon("/icons/shell.svg", Icons.class);
    @Nullable Icon Code = IconLoader.getIcon("/icons/codeSpan.svg", Icons.class);
    @Nullable Icon Run = IconLoader.getIcon("/icons/run.svg", Icons.class);
    @Nullable Icon Help = IconLoader.getIcon("/icons/help.svg", Icons.class);
}
