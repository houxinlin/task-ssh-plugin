package dev.cool.ssh.task.view;

import com.intellij.icons.AllIcons;
import com.intellij.ide.dnd.*;
import com.intellij.ide.dnd.aware.DnDAwareTree;
import com.intellij.ide.ui.UISettings;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.GraphicsConfig;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.ui.*;
import com.intellij.ui.awt.RelativeRectangle;
import com.intellij.ui.render.RenderingHelper;
import com.intellij.ui.tree.ui.DefaultTreeUI;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.EditSourceOnEnterKeyHandler;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import dev.cool.ssh.task.common.Icons;
import dev.cool.ssh.task.exec.ExecType;
import dev.cool.ssh.task.exec.State;
import dev.cool.ssh.task.exec.TaskExec;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.exec.wrapper.HostInfoWrapper;
import dev.cool.ssh.task.model.*;
import dev.cool.ssh.task.storage.TaskStorage;
import dev.cool.ssh.task.view.dialog.*;
import dev.cool.ssh.task.view.node.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class SSHTaskItem extends JPanel {
    private static final String NEW_COLOR_NAME = "NotificationsToolwindow.newNotification.background";
    private static final Color NEW_DEFAULT_COLOR = new JBColor(0xE6EEF7, 0x45494A);
    private static final JBColor NEW_COLOR = JBColor.namedColor(NEW_COLOR_NAME, NEW_DEFAULT_COLOR);
    private static final Color COOKIE_ITEM_BACKGROUND = NEW_COLOR;
    private Task task;
    private final Tree tree;
    private Project project;
    private final RootNode rootNode = new RootNode();


    public SSHTaskItem(Task task, Project project) {
        this.task = task;
        this.project = project;
        this.setLayout(new VerticalFlowLayout());
        JLabel titleLabel = new JLabel(task.getTaskName(), SwingConstants.LEFT);
        AnAction runAction = new AnAction("运行", "运行", Icons.Run) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                ApplicationManager.getApplication().executeOnPooledThread(new TaskExec(buildProgressTask(executionNode -> true)));
            }
        };
        AnAction deleteAction = new AnAction("删除", "删除", Icons.Delete) {
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

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.add(createNewActionButton(runAction));
        panel.add(createNewActionButton(deleteAction));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(panel, BorderLayout.EAST);

        // 添加到主面板
        this.add(titlePanel);
        tree = initTree();
        this.add(tree);
        loadTaskTree();
    }

    private HostNode createHostNode(HostInfo hostInfo) {
        if (Objects.equals(hostInfo.getHostType(), 2)) return new JumpNode(hostInfo);
        return new SimpleHostNode(hostInfo);
    }

    private ExecutionNode createExecuteNode(ExecuteInfo executeInfo) {
        if (Objects.equals(executeInfo.getExecuteType(), ExecType.UPLOAD.getExecType())) {
            return new ProgressExecuteNode(executeInfo, tree);
        }
        return new TimerExecuteNode(executeInfo, tree);
    }

    private MutableTreeNode createMutableTreeNode(HostInfo hostInfo) {
        HostNode hostNode = createHostNode(hostInfo);
        for (ExecuteInfo execute : hostInfo.getExecuteInfos()) {
            hostNode.add(createExecuteNode(execute));
        }
        return hostNode;
    }

    private static class DropHighlightTree extends DnDAwareTree {
        public DropHighlightTree(TreeModel model) {
            super(model);
        }

        @Override
        public Color getBackground() {
            return COOKIE_ITEM_BACKGROUND;
        }
    }

    private Tree initTree() {
        Tree tree = new DropHighlightTree(new DefaultTreeModel(rootNode));
        tree.setLargeModel(true);
        tree.putClientProperty(AnimatedIcon.ANIMATION_IN_RENDERER_ALLOWED, true);
        tree.putClientProperty(DefaultTreeUI.AUTO_EXPAND_ALLOWED, false);
        EditSourceOnEnterKeyHandler.install(tree);
        TreeSpeedSearch.installOn(tree).setComparator(new SpeedSearchComparator(false));
        TreeUtil.installActions(tree);
        tree.putClientProperty(RenderingHelper.SHRINK_LONG_RENDERER, true);
        tree.setBackground(COOKIE_ITEM_BACKGROUND);
        tree.setOpaque(false);
        tree.setCellRenderer(new ProgressNodeRenderer());

        // 添加拖拽支持
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        // 添加大小变化监听器

        DnDManager dndManager = DnDManager.getInstance();
        tree.setDragEnabled(true);

        dndManager.registerSource(new DnDSource() {
            @Override
            public boolean canStartDragging(DnDAction action, Point dragOrigin) {
                TreePath path = tree.getClosestPathForLocation(dragOrigin.x, dragOrigin.y);
                if (path == null) return false;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                return node != null && node != rootNode;
            }

            @Override
            public DnDDragStartBean startDragging(DnDAction action, Point dragOrigin) {
                TreePath path = tree.getClosestPathForLocation(dragOrigin.x, dragOrigin.y);
                if (path == null) return null;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node == null || node == rootNode) return null;
                return new DnDDragStartBean(node);
            }

            @Override
            public void dragDropEnd() {
                tree.repaint();
            }
        }, tree);

        dndManager.registerTarget(new DnDTarget() {
            @Override
            public boolean update(DnDEvent event) {
                Point point = event.getPoint();
                TreePath path = tree.getClosestPathForLocation(point.x, point.y);
                if (path == null) {
                    event.setDropPossible(false, "Invalid drop location");
                    return false;
                }

                DefaultMutableTreeNode target = (DefaultMutableTreeNode) path.getLastPathComponent();
                DefaultMutableTreeNode source = (DefaultMutableTreeNode) event.getAttachedObject();

                if (source == null) {
                    event.setDropPossible(false, "Invalid source node");
                    return false;
                }

                // 检查是否是ExecutionNode
                if (!(source instanceof ExecutionNode)) {
                    event.setDropPossible(false, "Only execution nodes can be dragged");
                    return false;
                }

                // 检查目标位置
                if (target instanceof HostNode) {
                    // 允许拖放到HostNode上
                    Rectangle bounds = tree.getPathBounds(path);
                    if (bounds != null) {
                        event.setHighlighting(new RelativeRectangle(tree, bounds), 1);
                    }
                    event.setDropPossible(true, "Drop here to copy execution");
                    return true;
                } else if (target instanceof ExecutionNode) {
                    // 检查是否在同一个HostNode下
                    DefaultMutableTreeNode sourceParent = (DefaultMutableTreeNode) source.getParent();
                    DefaultMutableTreeNode targetParent = (DefaultMutableTreeNode) target.getParent();

                    Rectangle bounds = tree.getPathBounds(path);
                    if (bounds != null) {
                        event.setHighlighting(new RelativeRectangle(tree, bounds), 1);
                    }

                    if (sourceParent == targetParent) {
                        event.setDropPossible(true, "Drop here to reorder");
                    } else {
                        event.setDropPossible(true, "Drop here to copy and insert");
                    }
                    return true;
                }

                event.setDropPossible(false, "Invalid drop target");
                return false;
            }

            @Override
            public void drop(DnDEvent event) {
                Point point = event.getPoint();
                TreePath path = tree.getClosestPathForLocation(point.x, point.y);
                if (path == null) return;

                DefaultMutableTreeNode target = (DefaultMutableTreeNode) path.getLastPathComponent();
                DefaultMutableTreeNode source = (DefaultMutableTreeNode) event.getAttachedObject();

                if (!(source instanceof ExecutionNode)) {
                    return;
                }

                if (target instanceof HostNode) {
                    // 复制到新的HostNode
                    ExecutionNode sourceNode = (ExecutionNode) source;
                    HostNode targetHostNode = (HostNode) target;

                    // 克隆ExecuteInfo
                    ExecuteInfo clonedExecuteInfo = sourceNode.getExecuteInfo().clone();

                    // 设置新的sort值
                    int maxSort = 0;
                    for (ExecuteInfo ei : ((HostInfo) targetHostNode.getUserObject()).getExecuteInfos()) {
                        if (ei.getSort() > maxSort) {
                            maxSort = ei.getSort();
                        }
                    }
                    clonedExecuteInfo.setSort(maxSort + 1);

                    // 添加到HostInfo
                    ((HostInfo) targetHostNode.getUserObject()).getExecuteInfos().add(clonedExecuteInfo);

                    // 创建新的ExecutionNode并添加到树中
                    ExecutionNode newExecNode = createExecuteNode(clonedExecuteInfo);
                    targetHostNode.add(newExecNode);

                    // 更新树模型
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    model.nodeStructureChanged(targetHostNode);

                    updateSortValues(target);
                } else if (target instanceof ExecutionNode) {
                    // 在同一个HostNode下重新排序或复制到其他HostNode
                    DefaultMutableTreeNode sourceParent = (DefaultMutableTreeNode) source.getParent();
                    DefaultMutableTreeNode targetParent = (DefaultMutableTreeNode) target.getParent();

                    if (sourceParent == targetParent) {
                        // 同一HostNode下重排序
                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                        int targetIndex = targetParent.getIndex(target);
                        model.removeNodeFromParent(source);
                        model.insertNodeInto(source, targetParent, targetIndex);
                        updateSortValues(targetParent);
                    } else {
                        // 复制到其他HostNode的指定位置
                        ExecutionNode sourceNode = (ExecutionNode) source;
                        HostNode targetHostNode = (HostNode) targetParent;

                        // 克隆ExecuteInfo
                        ExecuteInfo clonedExecuteInfo = sourceNode.getExecuteInfo().clone();

                        // 获取目标位置
                        int targetIndex = targetHostNode.getIndex(target);

                        // 设置新的sort值
                        clonedExecuteInfo.setSort(targetIndex);

                        // 添加到HostInfo
                        ((HostInfo) targetHostNode.getUserObject()).getExecuteInfos().add(targetIndex, clonedExecuteInfo);

                        // 创建新的ExecutionNode并添加到树中
                        ExecutionNode newExecNode = createExecuteNode(clonedExecuteInfo);
                        targetHostNode.insert(newExecNode, targetIndex);

                        // 更新树模型
                        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                        model.nodeStructureChanged(targetHostNode);
                        updateSortValues(sourceParent);
                    }
                    TreeUtil.expandAll(tree);
                }
            }
        }, tree);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        Object selectedNode = path.getLastPathComponent();
                        if (selectedNode instanceof DefaultMutableTreeNode node) {
                            DialogWrapper editorDialog = createEditorDialog(node);
                            if (editorDialog != null) {
                                e.consume();
                                editorDialog.show();
                            }
                        }
                    }
                }
            }
        });
        tree.addMouseListener(new PopupHandler() {
            @Override
            public void invokePopup(Component component, int x, int y) {
                DefaultActionGroup group = new DefaultActionGroup();
                List<RootNodeObject> nodes =
                        TreeUtil.collectSelectedObjects(tree, (path) -> TreeUtil.getLastUserObject(RootNodeObject.class, path));
                if (nodes.size() == 1) {
                    group.add(new AddHostAction());
                }
                List<HostInfo> hostInfos =
                        TreeUtil.collectSelectedObjects(tree, (path) -> TreeUtil.getLastUserObject(HostInfo.class, path));

                if (!hostInfos.isEmpty()) {
                    group.add(new ExecNodeAnAction());
                    group.addSeparator();
                    group.add(new AddTaskAction(hostInfos.get(0)));
                    group.add(new DeleteHostAction());
                    group.add(new EditHostAnAction());
                }
                List<ExecuteInfo> executeInfos =
                        TreeUtil.collectSelectedObjects(tree, (path) -> TreeUtil.getLastUserObject(ExecuteInfo.class, path));

                if (!executeInfos.isEmpty()) {
                    group.add(new ExecNodeAnAction());
                    group.addSeparator();
                    group.add(new DeleteTaskAction());
                }
                ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("TaskView", group);
                popupMenu.setTargetComponent(tree);
                JPopupMenu menu = popupMenu.getComponent();
                menu.show(component, x, y);
            }
        });
        return tree;
    }

    private class ExecNodeAnAction extends AnAction {
        public ExecNodeAnAction() {
            super("Run");
            getTemplatePresentation().setIcon(Icons.Run);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            Set<ExecutionNode> executionNodes = new HashSet<>();
            for (TreePath collectSelectedPath : TreeUtil.collectSelectedPaths(tree)) {
                if (collectSelectedPath.getLastPathComponent() instanceof ExecutionNode e) {
                    executionNodes.add(e);
                }
                if (collectSelectedPath.getLastPathComponent() instanceof HostNode node) {
                    for (int i = 0; i < node.getChildCount(); i++) {
                        executionNodes.add(((ExecutionNode) node.getChildAt(i)));
                    }
                }
            }
            List<HostInfoWrapper> hostInfoWrappers = buildProgressTask(executionNodes::contains);
            ApplicationManager.getApplication().executeOnPooledThread(new TaskExec(hostInfoWrappers));
        }
    }

    private class DeleteTaskAction extends AnAction {
        public DeleteTaskAction() {
            super("Delete");
            getTemplatePresentation().setIcon(Icons.Delete);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            for (TreePath selectPath : TreeUtil.collectSelectedPaths(tree)) {
                if (selectPath.getLastPathComponent() instanceof ExecutionNode executionNode) {
                    TreeNode parent = executionNode.getParent();
                    if (parent instanceof HostNode hostNode) {
                        hostNode.remove(executionNode);
                        ((HostInfo) hostNode.getUserObject()).getExecuteInfos().remove(executionNode.getExecuteInfo());
                        model.nodeStructureChanged(hostNode);
                    }
                }
            }
        }
    }

    private class DeleteHostAction extends AnAction {
        public DeleteHostAction() {
            super("Delete");
            getTemplatePresentation().setIcon(Icons.Delete);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            List<HostNode> hostNodes = TreeUtil.collectSelectedObjects(tree, path -> {
                if (path.getLastPathComponent() instanceof HostNode hostNode) {
                    return hostNode;
                }
                return null;
            });
            for (HostNode hostNode : hostNodes) {
                rootNode.remove(hostNode);
                task.getHosts().remove(hostNode.getUserObject());
                model.nodeStructureChanged(rootNode);
            }
        }
    }

    private DialogWrapper createEditorDialog(DefaultMutableTreeNode node) {
        Object object = node.getUserObject();
        if (object instanceof ExecuteInfo executeInfo) {
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.UPLOAD.getExecType())) {
                FileMapChooseDialog fileMapChooseDialog = new FileMapChooseDialog(project, executeInfo);
                fileMapChooseDialog.setHostInfo(((HostNode) node.getParent()).getHostInfo());
                return fileMapChooseDialog;
            }
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.KILL_JAR.getExecType())) {
                return new KillJarParameterDialog(project, executeInfo);
            }
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.COMMAND.getExecType())) {
                return new CommandParameterDialog(project, executeInfo);
            }
            if (Objects.equals(executeInfo.getExecuteType(), ExecType.SCRIPT.getExecType())) {
                return new ScriptPathChooseDialog(project, executeInfo);
            }
        }
        if (object instanceof HostInfo hostInfo) {
            if (hostInfo.getHostType() == 2) {
                return new JumpServerSSHConfigDialog(project, hostInfo);
            }
            return new SimpleHostInfoConfigDialog(project, hostInfo);
        }
        return null;
    }

    private void addExecuteInfoToHosts(ExecuteInfo executeInfo) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        for (TreePath collectSelectedPath : TreeUtil.collectSelectedPaths(tree)) {
            if (collectSelectedPath.getLastPathComponent() instanceof HostNode hostNode) {
                ExecuteInfo cloned = executeInfo.clone();
                hostNode.getHostInfo().getExecuteInfos().add(cloned);
                // 直接插入到tree
                ExecutionNode execNode = createExecuteNode(cloned);
                hostNode.add(execNode);
                model.nodeStructureChanged(hostNode);
                updateSortValues(hostNode);
            }
        }
        TreeUtil.expandAll(tree);
    }

    private class AddTaskAction extends DefaultActionGroup {
        public AddTaskAction(HostInfo hostInfo) {
            super("Add Task", "", AllIcons.General.Add);
            setPopup(true);
            addAll(new AnAction("文件上传", "", AllIcons.Actions.Upload) {
                       @Override
                       public void actionPerformed(@NotNull AnActionEvent e) {
                           FileMapChooseDialog fileMapChooseDialog = new FileMapChooseDialog(e.getProject());
                           fileMapChooseDialog.setHostInfo(hostInfo);
                           fileMapChooseDialog.show();
                           if (fileMapChooseDialog.isOK()) {
                               File file = new File(fileMapChooseDialog.getLocalPath());
                               FileExecuteInfo fileExecuteInfo = new FileExecuteInfo();
                               fileExecuteInfo.setLocalPath(fileMapChooseDialog.getLocalPath());
                               fileExecuteInfo.setRemotePath(fileMapChooseDialog.getRemotePath());

                               ExecuteInfo executeInfo = new ExecuteInfoBuilder()
                                       .executeType(ExecType.UPLOAD.getExecType())
                                       .executeName("Upload " + file.getName())
                                       .executeExtJSON(fileExecuteInfo)
                                       .build();
                               addExecuteInfoToHosts(executeInfo);
                           }
                       }
                   },
                    new AnAction("终止jar进程", "", AllIcons.Debugger.KillProcess) {
                        @Override
                        public void actionPerformed(AnActionEvent e) {
                            KillJarParameterDialog killJarParameterDialog = new KillJarParameterDialog(e.getProject());
                            killJarParameterDialog.show();
                            if (killJarParameterDialog.isOK()) {
                                SimpleParameter simpleParameter = new SimpleParameter();
                                simpleParameter.setValue(killJarParameterDialog.getJarName());

                                ExecuteInfo executeInfo = new ExecuteInfoBuilder()
                                        .executeType(ExecType.KILL_JAR.getExecType())
                                        .executeName("Kill jar " + killJarParameterDialog.getJarName())
                                        .executeExtJSON(simpleParameter)
                                        .build();
                                addExecuteInfoToHosts(executeInfo);
                            }
                        }
                    },
                    new AnAction("执行命令", "", Icons.Code) {
                        @Override
                        public void actionPerformed(AnActionEvent e) {
                            CommandParameterDialog commandParameterDialog = new CommandParameterDialog(e.getProject());
                            commandParameterDialog.show();
                            if (commandParameterDialog.isOK()) {
                                SimpleParameter simpleParameter = new SimpleParameter();
                                simpleParameter.setValue(commandParameterDialog.getCommand());

                                ExecuteInfo executeInfo = new ExecuteInfoBuilder()
                                        .executeType(ExecType.COMMAND.getExecType())
                                        .executeName("执行命令 " + commandParameterDialog.getCommand())
                                        .executeExtJSON(simpleParameter)
                                        .build();
                                addExecuteInfoToHosts(executeInfo);
                            }
                        }
                    },
                    new AnAction("执行脚本", "", Icons.Shell) {
                        @Override
                        public void actionPerformed(AnActionEvent e) {
                            ScriptPathChooseDialog scriptPathChooseDialog = new ScriptPathChooseDialog(e.getProject());
                            scriptPathChooseDialog.show();
                            if (scriptPathChooseDialog.isOK()) {
                                ScriptParameter simpleParameter = new ScriptParameter();
                                simpleParameter.setValue(scriptPathChooseDialog.getPath());
                                simpleParameter.setExecuteInScriptDir(scriptPathChooseDialog.isExecuteInScriptDir());
                                ExecuteInfo executeInfo = new ExecuteInfoBuilder()
                                        .executeType(ExecType.SCRIPT.getExecType())
                                        .executeName("执行脚本 " + scriptPathChooseDialog.getPath())
                                        .executeExtJSON(simpleParameter)
                                        .build();
                                addExecuteInfoToHosts(executeInfo);
                            }
                        }
                    });
        }
    }

    private class AddHostAction extends DefaultActionGroup {
        public AddHostAction() {
            super("Add Host", "", AllIcons.General.Add);
            setPopup(true);
            addAction(new AnAction("Ssh") {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    SimpleHostInfoConfigDialog simpleHostInfoConfigDialog = new SimpleHostInfoConfigDialog(e.getProject());
                    simpleHostInfoConfigDialog.show();
                    if (simpleHostInfoConfigDialog.isOK()) {
                        HostInfo hostInfo = simpleHostInfoConfigDialog.buildHost();
                        task.getHosts().add(hostInfo);
                        loadTaskTree();
                    }
                }
            });
            addAction(new AnAction("Jump Server") {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    JumpServerSSHConfigDialog jumpServerSSHConfigDialog = new JumpServerSSHConfigDialog(e.getProject());
                    jumpServerSSHConfigDialog.show();
                    if (jumpServerSSHConfigDialog.isOK()) {
                        HostInfo hostInfo = jumpServerSSHConfigDialog.buildHost();
                        task.getHosts().add(hostInfo);
                        loadTaskTree();
                    }
                }
            });
        }
    }

    private void loadTaskTree() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        rootNode.removeAllChildren();

        // 根据sort排序HostInfo
        task.getHosts().sort(Comparator.comparingInt(HostInfo::getSort));

        for (HostInfo host : task.getHosts()) {
            // 根据sort排序ExecuteInfo
            host.getExecuteInfos().sort(Comparator.comparingInt(ExecuteInfo::getSort));
            rootNode.add(createMutableTreeNode(host));
        }

        model.nodeChanged(rootNode);
        model.nodeStructureChanged(rootNode);
        TreeUtil.expandAll(tree);
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

    private List<HostInfoWrapper> buildProgressTask(Function<ExecutionNode, Boolean> filter) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        List<HostInfoWrapper> hosts = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root.getChildAt(i) instanceof HostNode hostNode) {
                List<ExecuteInfoWrapper> executeInfoWrappers = new ArrayList<>();
                hosts.add(new HostInfoWrapper(hostNode, executeInfoWrappers));
                for (int j = 0; j < hostNode.getChildCount(); j++) {
                    TreeNode executeNode = hostNode.getChildAt(j);
                    if (executeNode instanceof ExecutionNode executionNode
                            && filter.apply(executionNode)) {
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
            if (value instanceof TaskNode taskNode) {
                setIcon(taskNode.getIcon());
            }
            setPreferredSize(new Dimension(tree.getWidth(), tree.getRowHeight()));

            this.myDurationText = null;
            this.myDurationColor = null;
            this.myDurationWidth = 0;
            this.myDurationLeftInset = 0;
            this.myDurationRightInset = 0;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node instanceof ExecutionNode executionNode) {
                setIcon(executionNode.getState().getIcon());
                this.myDurationText = executionNode.getDurationText();
                if (this.myDurationText != null && !this.myDurationText.isEmpty()) {
                    FontMetrics metrics = this.getFontMetrics(RelativeFont.SMALL.derive(this.getFont()));
                    this.myDurationWidth = metrics.stringWidth(this.myDurationText);
                    this.myDurationLeftInset = metrics.getHeight() / 4;
                    this.myDurationRightInset = this.myDurationLeftInset;
                    this.myDurationColor = selected ? UIUtil.getTreeSelectionForeground(hasFocus) : SimpleTextAttributes.GRAYED_ATTRIBUTES.getFgColor();
                }
            }
        }

        @Override
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


    private static class RootNodeObject {

    }

    private class RootNode extends TaskNode {
        public RootNode() {
            super(new RootNodeObject());
        }

        @Override
        public String toString() {
            return task.getHosts().size() + "个主机";
        }

        @Override
        public Icon getIcon() {
            return Icons.TaskGroup;
        }
    }

    private void updateSortValues(DefaultMutableTreeNode parent) {
        if (parent == rootNode) {
            // 更新HostInfo的sort
            for (int i = 0; i < parent.getChildCount(); i++) {
                HostNode hostNode = (HostNode) parent.getChildAt(i);
                ((HostInfo) hostNode.getUserObject()).setSort(i);
            }
        } else if (parent instanceof HostNode) {
            // 更新ExecuteInfo的sort
            for (int i = 0; i < parent.getChildCount(); i++) {
                ExecutionNode execNode = (ExecutionNode) parent.getChildAt(i);
                execNode.getExecuteInfo().setSort(i);
            }
        }
    }

    private class EditHostAnAction extends AnAction {
        public EditHostAnAction() {
            super("Edit", "Edit", AllIcons.Actions.Edit);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            List<Object> objects = TreeUtil.collectSelectedUserObjects(tree);
            if (!objects.isEmpty()) {
//                DialogWrapper editorDialog = createEditorDialog(objects.get(0));
//                if (editorDialog != null) editorDialog.show();
            }
        }
    }


}
