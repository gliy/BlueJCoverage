package bluej.codecoverage.ui;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.join.ClassInfo;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

/**
 * Main display for the results of the code coverage.
 * 
 * @author Ian
 */
public class CoverageReportFrame extends JFrame
{
    List<BCoveragePackage> coverage;
    private JTabbedPane tabs;
    private CoverageOverviewPane overview;
    private Map<String, JScrollPane> classToDisplay;
    CurrentPreferences prefs = CoveragePrefManager.getPrefs()
        .get();

    public CoverageReportFrame(List<BCoveragePackage> classesCovered)
        throws ProjectNotOpenException, PackageNotFoundException,
        BadLocationException
    {

        this.coverage = classesCovered;
        setSize(700, 500);
        generateTabs();
        // generateDisplay(classesCovered);
        // pack();

        setTitle("Coverage Report");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void generateTabs()
    {
        classToDisplay = new HashMap<String, JScrollPane>();
        tabs = new JTabbedPane();
        // add(tabs, BorderLayout.CENTER);
        overview = new CoverageOverviewPane(coverage, prefs);
        
        JScrollPane pane = new JScrollPane(overview);
        pane.setPreferredSize(new Dimension(getWidth(), 100));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs,
            overview);

        add(split);
        split.setDividerLocation(getWidth() / 2);
        split.setOneTouchExpandable(true);
        overview.addTreeSelectionListener(new TreeListener());
    }

    private void bringUpTab(BCoverageClass clz)
    {
        try
        {
            ClassInfo bclass = clz.getClassInfo();
            JScrollPane existingDisplay = classToDisplay.get(clz.getId());
            if (existingDisplay == null)
            {
                existingDisplay =new CoverageSourceDisplay(clz);
                classToDisplay.put(clz.getId(), existingDisplay);

                tabs.add(existingDisplay, bclass.getName());
            }
            tabs.setSelectedComponent(existingDisplay);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    class TreeListener implements TreeSelectionListener
    {

        @Override
        public void valueChanged(TreeSelectionEvent e)
        {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overview.getLastSelectedPathComponent();
            if (selectedNode.getUserObject() instanceof BCoverageClass)
            {
                BCoverageClass bClassInfo = (BCoverageClass) selectedNode.getUserObject();
                bringUpTab(bClassInfo);
            }
        }
    }
}
