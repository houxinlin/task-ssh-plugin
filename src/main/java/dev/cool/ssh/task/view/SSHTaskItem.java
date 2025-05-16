package dev.cool.ssh.task.view;

import com.google.gson.Gson;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.UISettings;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.GraphicsConfig;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.ui.*;
import com.intellij.ui.components.panels.OpaquePanel;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import dev.cool.ssh.task.exec.State;
import dev.cool.ssh.task.exec.TaskExec;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.exec.wrapper.HostInfoWrapper;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.FileExecuteInfo;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.Task;
import dev.cool.ssh.task.storage.TaskStorage;
import dev.cool.ssh.task.view.dialog.FileMapChooseDialog;
import dev.cool.ssh.task.view.dialog.JumpServerSSHConfigDialog;
import dev.cool.ssh.task.view.node.ExecutionNode;
import dev.cool.ssh.task.view.node.JumpNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SSHTaskItem extends JPanel {
    private static final String NEW_COLOR_NAME = "NotificationsToolwindow.newNotification.background";
    private static final Color NEW_DEFAULT_COLOR = new JBColor(0xE6EEF7, 0x45494A);
    private static final JBColor NEW_COLOR = JBColor.namedColor(NEW_COLOR_NAME, NEW_DEFAULT_COLOR);
    private static final Color COOKIE_ITEM_BACKGROUND = NEW_COLOR;
    private final JLabel hostCountLabel;
    private final JLabel taskCountLabel;
    private Task task;
    private Tree tree;

    public SSHTaskItem(Task task) {
        this(task.getTaskName(),
                task.getHosts() == null ? 0 : task.getHosts().size(),
                task.getExecutes() == null ? 0 : task.getExecutes().size());
        this.task = task;

        loadTaskTree();
    }

    private void reloadTaskItem() {
        setTaskCount(task.getExecutes() == null ? 0 : task.getExecutes().size());
        setHostCount(task.getHosts() == null ? 0 : task.getHosts().size());
    }

    public SSHTaskItem(String title, int hostCount, int taskCount) {
        this.setLayout(new VerticalFlowLayout());
        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        this.hostCountLabel = new JLabel("<html>主机数量:<u>" + hostCount + "</u></html>", SwingConstants.LEFT);
        this.hostCountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.taskCountLabel = new JLabel("<html>任务数量:<u>" + taskCount + "</u></html>", SwingConstants.LEFT);
        this.taskCountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // 主机加号
        AnAction hostAddAction = new AnAction(null, "添加主机", AllIcons.Expui.General.Add) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                DefaultActionGroup defaultActionGroup = new DefaultActionGroup(
                        new AnAction("Ssh") {
                            @Override
                            public void actionPerformed(AnActionEvent e) {
                            }
                        },
                        new AnAction("Jump Server") {
                            @Override
                            public void actionPerformed(AnActionEvent e) {
                                JumpServerSSHConfigDialog jumpServerSSHConfigDialog = new JumpServerSSHConfigDialog(e.getProject());
                                jumpServerSSHConfigDialog.show();
                                if (jumpServerSSHConfigDialog.isOK()) {
                                    HostInfo hostInfo = jumpServerSSHConfigDialog.buildHost();
                                    List<HostInfo> tasks = task.getHosts();
                                    tasks.add(hostInfo);
                                    reloadTaskItem();
                                }
                            }
                        }
                );
                JBPopupFactory.getInstance().createActionGroupPopup(
                                null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                                false, null, 10, null, "popup@RefreshAction")
                        .showUnderneathOf(e.getInputEvent().getComponent());

            }
        };
        // 任务加号
        AnAction taskAddAction = new AnAction(null, "添加任务", AllIcons.Expui.General.Add) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                DefaultActionGroup defaultActionGroup = new DefaultActionGroup(
                        new AnAction("文件上传") {
                            @Override
                            public void actionPerformed(@NotNull AnActionEvent e) {
                                FileMapChooseDialog fileMapChooseDialog = new FileMapChooseDialog(e.getProject());
                                fileMapChooseDialog.show();
                                if (fileMapChooseDialog.isOK()) {
                                    ExecuteInfo executeInfo = new ExecuteInfo();
                                    executeInfo.setExecuteType(1);
                                    File file = new File(fileMapChooseDialog.getLocalPath());
                                    executeInfo.setExecuteName("Upload " + file.getName() + " to " + fileMapChooseDialog.getRemotePath());
                                    FileExecuteInfo fileExecuteInfo = new FileExecuteInfo();
                                    fileExecuteInfo.setLocalPath(fileMapChooseDialog.getLocalPath());
                                    fileExecuteInfo.setRemotePath(fileMapChooseDialog.getRemotePath());
                                    executeInfo.setExecuteExtJSON(new Gson().toJson(fileExecuteInfo));
                                    task.getExecutes().add(executeInfo);
                                    reloadTaskItem();
                                }
                            }
                        },
                        new AnAction("kill进程") {
                            @Override
                            public void actionPerformed(AnActionEvent e) {
                                // TODO: kill进程逻辑
                            }
                        },
                        new AnAction("执行脚本") {
                            @Override
                            public void actionPerformed(AnActionEvent e) {
                                // TODO: 执行脚本逻辑
                            }
                        }
                );
                JBPopupFactory.getInstance().createActionGroupPopup(
                                null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                                false, null, 10, null, "popup@RefreshAction")
                        .showUnderneathOf(e.getInputEvent().getComponent());

            }
        };

        // 主机面板
        JPanel hostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hostPanel.setOpaque(false);
        hostPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        hostPanel.add(hostCountLabel);
        hostPanel.add(createNewActionButton(hostAddAction));

        // 任务面板
        JPanel taskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        taskPanel.setOpaque(false);
        taskPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        taskPanel.add(taskCountLabel);
        taskPanel.add(createNewActionButton(taskAddAction));

        // 顶部标题和右侧ActionButton
        AnAction runAction = new AnAction("运行", "运行", AllIcons.Actions.Execute) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                ApplicationManager.getApplication().executeOnPooledThread(new TaskExec(buildProgressTask()));
            }
        };
        AnAction deleteAction = new AnAction("删除", "删除", AllIcons.General.Delete) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                TaskStorage.getInstance().getTasks().remove(task);
                Container parent = getParent();
                if (parent != null) {
                    parent.remove(SSHTaskItem.this);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        };

        JPanel panel = new OpaquePanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(createNewActionButton(runAction));
        panel.add(createNewActionButton(deleteAction));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(panel, BorderLayout.EAST);

        // 添加到主面板
        this.add(titlePanel);
        this.add(hostPanel);
        this.add(taskPanel);

        tree = new SimpleTree();
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new ProgressNodeRenderer());
        tree.putClientProperty(AnimatedIcon.ANIMATION_IN_RENDERER_ALLOWED, true);
        tree.setBackground(COOKIE_ITEM_BACKGROUND);
        CollapsiblePanel aa = new CollapsiblePanel(tree, false, false, AllIcons.Actions.Collapseall, AllIcons.Actions.Expandall, "Progress");
        this.add(aa);
    }

    private MutableTreeNode createMutableTreeNode(HostInfo hostInfo) {
        JumpNode jumpNode = new JumpNode(hostInfo);
        for (ExecuteInfo execute : task.getExecutes()) {
            jumpNode.add(new ExecutionNode(execute, tree));
        }
        return jumpNode;
    }

    private void loadTaskTree() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        for (HostInfo host : task.getHosts()) {
            root.add(createMutableTreeNode(host));
        }
        model.nodeChanged(root);

    }

    public void setHostCount(int hostCount) {
        this.hostCountLabel.setText("<html>主机数量:<u> " + hostCount + "</u></html>");
    }

    public void setTaskCount(int taskCount) {
        this.taskCountLabel.setText("<html>任务数量: <u>" + taskCount + "</u></html>");
    }

    private ActionButton createNewActionButton(AnAction action) {
        return new ActionButton(action, action.getTemplatePresentation(), "hostAdd", new Dimension(24, 24));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(COOKIE_ITEM_BACKGROUND);
        GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        int cornerRadius = 15;
        g.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        config.restore();
    }

    private List<HostInfoWrapper> buildProgressTask() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        List<HostInfoWrapper> hosts = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            TreeNode hostNode = root.getChildAt(i);
            if (hostNode instanceof JumpNode jumpNode) {
                List<ExecuteInfoWrapper> executeInfoWrappers = new ArrayList<>();
                hosts.add(new HostInfoWrapper(jumpNode, executeInfoWrappers));
                for (int j = 0; j < jumpNode.getChildCount(); j++) {
                    TreeNode executeNode = jumpNode.getChildAt(j);
                    if (executeNode instanceof ExecutionNode executionNode) {
                        executionNode.setState(State.WAITING);
                        ExecuteInfoWrapper executeInfoWrapper = new ExecuteInfoWrapper(executionNode, executionNode.getExecuteInfo());
                        executeInfoWrappers.add(executeInfoWrapper);
                    }
                }
            }
        }
        return hosts;
    }

    public static class ProgressNodeRenderer extends NodeRenderer {
        private String myDurationText;
        private Color myDurationColor;
        private int myDurationWidth;
        private int myDurationLeftInset;
        private int myDurationRightInset;

        @Override
        public void customizeCellRenderer(@NotNull JTree tree, @NlsSafe Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);

            this.myDurationText = null;
            this.myDurationColor = null;
            this.myDurationWidth = 0;
            this.myDurationLeftInset = 0;
            this.myDurationRightInset = 0;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node instanceof ExecutionNode executionNode) {
                setIcon(executionNode.getState().getIcon());
                this.myDurationText = "aaa";
                if (this.myDurationText != null) {
                    FontMetrics metrics = this.getFontMetrics(RelativeFont.SMALL.derive(this.getFont()));
                    this.myDurationWidth = metrics.stringWidth(this.myDurationText);
                    this.myDurationLeftInset = metrics.getHeight() / 4;
                    this.myDurationRightInset = ExperimentalUI.isNewUI() ? tree.getInsets().right + JBUI.scale(4) : this.myDurationLeftInset;
                    this.myDurationColor = selected ? UIUtil.getTreeSelectionForeground(hasFocus) : SimpleTextAttributes.GRAYED_ATTRIBUTES.getFgColor();
                }
            }
        }

        protected void paintComponent(Graphics g) {
            UISettings.setupAntialiasing(g);
            Shape clip = null;
            int width = this.getWidth();
            int height = this.getHeight();
            if (this.isOpaque()) {
                g.setColor(this.getBackground());
                g.fillRect(0, 0, width, height);
            }

            if (this.myDurationWidth > 0) {
                width -= this.myDurationWidth + this.myDurationLeftInset + this.myDurationRightInset;
                if (width > 0 && height > 0) {
                    g.setColor(this.myDurationColor);
                    g.setFont(RelativeFont.SMALL.derive(this.getFont()));
                    g.drawString(this.myDurationText, width + this.myDurationLeftInset, getTextBaseLine(g.getFontMetrics(), height));
                    clip = g.getClip();
                    g.clipRect(0, 0, width, height);
                }
            }

            super.paintComponent(g);
            if (clip != null) {
                g.setClip(clip);
            }

        }
    }


}
