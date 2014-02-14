package bluej.codecoverage.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoverageInformation;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoverageCounter;

/**
 * Displays an overview of the code coverage for a class in a tree structure. Allows
 * the user to click on an item in the tree to open an editor with the source code.
 * 
 * @author ikingsbu
 * 
 */
class CoverageOverviewPane extends JTree
{

    private CurrentPreferences prefs;
    private List<BCoveragePackage> coverage;
    private DefaultTreeModel model;
    public CoverageOverviewPane(List<BCoveragePackage> coverage, CurrentPreferences prefs)
    {
        super();
        this.coverage = coverage;
        this.prefs = prefs;
        model = new DefaultTreeModel(createRootNode(coverage));
        setModel(model);
        UIManager.put("ProgressBar.foreground", new Color(58, 242, 70)); // green
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);


        setRootVisible(false);
        setRowHeight(0);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer()
        {
            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus)
            {
                Component defaultDisplay = super.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);
                Object treeNode = ((DefaultMutableTreeNode) value).getUserObject();

                if (treeNode instanceof BCoverageInformation)
                {

                    JPanel rtn = new JPanel();
                    BCoverageInformation info = (BCoverageInformation) treeNode;
                    ImageIcon iconToUse = getDisplayIcon(info);
                    if (iconToUse != null)
                    {
                        setIcon(iconToUse);
                    }
                    setText(info.getName());

                    CoverageCounter counter = info.getObjectCoverage();
                    JProgressBar progress = new JProgressBar(0,
                        counter.getTotal());
                    progress.setPreferredSize(new Dimension(
                        (int) progress.getPreferredSize()
                            .getWidth(), this.getHeight()));
                    progress.setValue(counter.getCovered());

                    progress.setStringPainted(true);
                    rtn.add(this);
                    rtn.add(progress);
                    rtn.setBackground(Color.WHITE);
                    return rtn;
                }
                else
                {
                    return defaultDisplay;
                }

            }
        };
        getSelectionModel()
            .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setToggleClickCount(1);

        setCellRenderer(renderer);
    }

    private static MutableTreeNode createRootNode(List<BCoveragePackage> coverage)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("All");
        Collections.sort(coverage, BCoverageBridge.SORT_BY_COVERAGE);
        for (BCoveragePackage pack : coverage)
        {
            root.add(createNode(pack));
        }
        return root;
    }

    private static DefaultMutableTreeNode createNode(BCoverageInformation pack)
    {
        DefaultMutableTreeNode packNode = new DefaultMutableTreeNode(pack);

        for (BCoverageInformation clz : pack.getNodes())
        {
            packNode.add(createNode(clz));
        }

        return packNode;
    }

    private ImageIcon getDisplayIcon(BCoverageInformation coverage)
    {
        ImageIcon rtn = null;
        if (coverage instanceof BCoveragePackage)
        {
            rtn = prefs.getPackageIcon();
        }
        else if (coverage instanceof BCoverageClass)
        {
            rtn = prefs.getSourceIcon();
        }
        return rtn;
    }

}