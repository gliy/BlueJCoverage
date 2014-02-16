package bluej.codecoverage.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoverage;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.join.ClassInfo;
import bluej.codecoverage.utils.join.BCoverageClass.BCoverageMethod;
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
    private Map<String, CoverageSourceDisplay> classToDisplay;
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
        classToDisplay = new HashMap<String, CoverageSourceDisplay>();
        tabs = new JTabbedPane();
        // add(tabs, BorderLayout.CENTER);
        overview = new CoverageOverviewPane(coverage, prefs);

        overview.setPreferredSize(new Dimension(getWidth(), 100));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs,
            overview);

        add(split);
        split.setDividerLocation((getWidth() / 2) + 30);
        split.setOneTouchExpandable(true);
        overview.addListener(new TreeListener());
    }

    private void bringUpTab(BCoverageClass clz)
    {
        try
        {
            ClassInfo bclass = clz.getClassInfo();
            CoverageSourceDisplay existingDisplay = classToDisplay.get(clz.getId());
            if (existingDisplay == null)
            {
                final CoverageSourceDisplay newDisplay = new CoverageSourceDisplay(clz);
                classToDisplay.put(newDisplay.getId(), newDisplay);
                newDisplay.getSource().addKeyListener(new KeyAdapter()
                {
    

                    @Override
                    public void keyPressed(KeyEvent e)
                    {
                        System.out.println(e);
                        if (e.getKeyCode() == KeyEvent.VK_W
                            && e.isControlDown())
                        {
                            CoverageSourceDisplay selected = (CoverageSourceDisplay) tabs.getSelectedComponent();
                            tabs.remove(selected);
                            classToDisplay.remove(selected.getId());
                        }
                    }
                });
                tabs.add(newDisplay, bclass.getName());
                existingDisplay = newDisplay;
            }
            tabs.setSelectedComponent(existingDisplay);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void moveCaret(DefaultMutableTreeNode node)
    {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        bringUpTab((BCoverageClass)parent.getUserObject());
        BCoverageMethod selectedCoverage = (BCoverageMethod) node.getUserObject();
        
       
            ((CoverageSourceDisplay) tabs.getSelectedComponent()).moveCaret(selectedCoverage
                .getFirstLine());
        
    }
    class TreeListener implements TreeSelectionListener
    {

        @Override
        public void valueChanged(TreeSelectionEvent e)
        {
            DefaultMutableTreeNode node = overview.getSelectedNode();
            BCoverage<?> selectedCoverage = (BCoverage<?>) node.getUserObject();
            if (selectedCoverage instanceof BCoverageClass)
            {
                BCoverageClass bClassInfo = (BCoverageClass) selectedCoverage;
                bringUpTab(bClassInfo);
            }
            else if (selectedCoverage instanceof BCoverageMethod)
            {
                moveCaret(node);
            }
        }
    }
    
}
