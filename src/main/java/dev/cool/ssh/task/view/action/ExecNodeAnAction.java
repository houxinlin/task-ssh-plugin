package dev.cool.ssh.task.view.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import dev.cool.ssh.task.exec.State;
import dev.cool.ssh.task.exec.TaskExec;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.exec.wrapper.HostInfoWrapper;
import dev.cool.ssh.task.view.node.ExecutionNode;
import dev.cool.ssh.task.view.node.HostNode;
import dev.cool.ssh.task.view.node.TaskNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class ExecNodeAnAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

    }
} 