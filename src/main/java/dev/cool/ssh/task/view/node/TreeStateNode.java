package dev.cool.ssh.task.view.node;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.Tree;
import dev.cool.ssh.task.exec.State;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class TreeStateNode extends DefaultMutableTreeNode {
    private State state = State.WAITING;
    private final Tree tree;
    private String errorMessage;


    public TreeStateNode(Object userObject, Tree tree) {
        super(userObject);
        this.tree = tree;
    }

    public State getState() {
        return state;
    }

    protected void nodeChanged(){
        ApplicationManager.getApplication().invokeLater(() -> ((DefaultTreeModel) tree.getModel()).nodeChanged(TreeStateNode.this));
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setState(State state) {
        this.state = state;
        nodeChanged();
    }
}
