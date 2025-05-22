package dev.cool.ssh.task.view.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.util.NlsSafe;
import dev.cool.ssh.task.model.FileEntry;
import dev.cool.ssh.task.view.node.FileNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class FileEntryTreeCellRenderer extends NodeRenderer {
    private static Map<String, Icon> iconMap = new HashMap<String, Icon>();

    public FileEntryTreeCellRenderer() {
        iconMap.put(".text", AllIcons.FileTypes.Text);
        iconMap.put(".zip", AllIcons.FileTypes.Archive);
        iconMap.put(".tar", AllIcons.FileTypes.Archive);
        iconMap.put(".gz", AllIcons.FileTypes.Archive);
        iconMap.put(".rar", AllIcons.FileTypes.Archive);
        iconMap.put(".7z", AllIcons.FileTypes.Archive);
        iconMap.put(".config", AllIcons.FileTypes.Config);
        iconMap.put(".cnf", AllIcons.FileTypes.Config);
        iconMap.put(".css", AllIcons.FileTypes.Css);
        iconMap.put(".html", AllIcons.FileTypes.Html);
        iconMap.put(".png", AllIcons.FileTypes.Image);
        iconMap.put(".jpg", AllIcons.FileTypes.Image);
        iconMap.put(".jpeg", AllIcons.FileTypes.Image);
        iconMap.put(".gif", AllIcons.FileTypes.Image);
        iconMap.put(".bmp", AllIcons.FileTypes.Image);
        iconMap.put(".ico", AllIcons.FileTypes.Image);
        iconMap.put(".jar", AllIcons.FileTypes.Java);
        iconMap.put(".java", AllIcons.FileTypes.JavaClass);
        iconMap.put(".js", AllIcons.FileTypes.JavaScript);
        iconMap.put(".sh", AllIcons.Nodes.Console);
        iconMap.put(".properties", AllIcons.FileTypes.Properties);
    }

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, @NlsSafe Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof FileNode node) {
            FileEntry userObject = (FileEntry) node.getUserObject();
            if (userObject.isDirectory()) {
                setIcon(AllIcons.Nodes.Folder);
            } else {
                setIcon(AllIcons.FileTypes.Any_type);
                for (String s : iconMap.keySet()) {
                    if (userObject.getFileName().toLowerCase().endsWith(s)) {
                        setIcon(iconMap.get(s));
                        break;
                    }
                }
            }
        }
    }
}