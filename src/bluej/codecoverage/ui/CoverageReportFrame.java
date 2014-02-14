package bluej.codecoverage.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import bluej.codecoverage.pref.CoverageConfigManager;
import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.ui.ext.LineAttributes;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoverageInformation;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.join.ClassInfo;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

/**
 * Main display for the results of the code coverage.
 * @author Ian
 */
public class CoverageReportFrame extends JFrame
{
    private List<BCoveragePackage> coverage;
    private JTabbedPane tabs;
    private CoverageOverviewPane overview;
    private JTree tree;
    private Map<String, JScrollPane> classToDisplay;
    private CurrentPreferences prefs = CoveragePrefManager.getPrefs().get();
    
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
        //add(tabs, BorderLayout.CENTER);
        overview = new CoverageOverviewPane();
        tree = overview.getTree();
        JScrollPane pane = new JScrollPane(tree);
        pane.setPreferredSize(new Dimension(getWidth(), 100));
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, tree);
       
        add(split);
        split.setDividerLocation(getWidth()/2);
        split.setOneTouchExpandable(true);
        tree.addTreeSelectionListener(new TreeListener());
    }

    private void bringUpTab(BCoverageClass clz)
    {
        try
        {
            ClassInfo bclass = clz.getClassInfo();
            JScrollPane existingDisplay = classToDisplay.get(clz.getId());
            if(existingDisplay == null) {
                existingDisplay = new JScrollPane(new CoverageClassDisplay(clz));
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
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode.getUserObject() instanceof BCoverageClass)
            {
                BCoverageClass bClassInfo = (BCoverageClass) selectedNode.getUserObject();
                bringUpTab(bClassInfo);
            }
        }
    }

    /**
     * Displays an overview of the code coverage for a class in
     *  a tree structure. Allows the user to click on an item in the tree to open
     *  an editor with the source code.
     * @author ikingsbu
     *
     */
    class CoverageOverviewPane
    {
        private JTree tree;

        public CoverageOverviewPane()
        {
            super();
            UIManager.put("ProgressBar.foreground", new Color(58, 242, 70)); // green
            UIManager.put("ProgressBar.selectionForeground", Color.BLACK);

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("All");
            Collections.sort(coverage, BCoverageBridge.SORT_BY_COVERAGE);
            for (BCoveragePackage pack : coverage)
            {
                root.add(createNode(pack));
            }

            tree = new JTree(root);
            tree.setRootVisible(false);
            tree.setRowHeight(0);
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
                        if(iconToUse != null) {
                            setIcon(iconToUse);                          
                        }
                        setText(info.getName());

                        CoverageCounter counter = info.getObjectCoverage();
                        JProgressBar progress = new JProgressBar(0, counter.getTotal());
                        progress.setPreferredSize(new Dimension((int) progress
                            .getPreferredSize().getWidth(), this.getHeight()));
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
            tree.getSelectionModel()
                .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.setToggleClickCount(1);

            tree.setCellRenderer(renderer);
        }

        public JTree getTree()
        {

            return tree;
        }

        private DefaultMutableTreeNode createNode(BCoverageInformation pack)
        {
            DefaultMutableTreeNode packNode = new DefaultMutableTreeNode(pack);

            for (BCoverageInformation clz : pack.getNodes())
            {
                packNode.add(createNode(clz));
            }

            return packNode;
        }
        
        private ImageIcon getDisplayIcon(BCoverageInformation coverage) {
            ImageIcon rtn = null;
            if(coverage instanceof BCoveragePackage) {
                rtn = prefs.getPackageIcon();
            } else if(coverage instanceof BCoverageClass) {
                rtn = prefs.getSourceIcon();
            }
            return rtn;
        }
        
    }
}
/**
 * Displays the source code for a file with the coverage information overlayed.
 * @author ikingsbu
 *
 */
class CoverageClassDisplay extends JTextPane
{
    private BCoverageClass coverage;
    private List<LineAttributes> lineAttributes;
    public CoverageClassDisplay(BCoverageClass coverage)
        throws ProjectNotOpenException, PackageNotFoundException,
        BadLocationException
    {
        super();
        this.coverage = coverage;
        this.lineAttributes = CoverageConfigManager.getConfig().getAllAttributes();
        generateDisplay();
    }

    private void generateDisplay() throws BadLocationException,
        ProjectNotOpenException, PackageNotFoundException
    {

        CoverageClass clz = coverage.getClassCoverage();
        ClassInfo bclz = coverage.getClassInfo();
        System.out.println(clz.getName());
        System.out.println("\t" + clz.getFirstLine() + "," + clz.getLastLine());

        StyledDocument doc = getStyledDocument();
        Map<Integer, AttributeSet> lineToStyle = createStyleMap(clz);

        for (int line = 0; line < bclz.getNumberOfLines(); line++)
        {

            String sourceCode = bclz.getLine(line);
            AttributeSet style = lineToStyle.get(line);
            doc.insertString(doc.getLength(), line + ": " + sourceCode + "\n",
                style);
            
        }
        
        setCaretPosition(0);
        ToolTipManager.sharedInstance().registerComponent(this);
        setEditable(false);

    }
    private Map<Integer , CoverageLine> lineStat;

	@Override
	public String getToolTipText(MouseEvent arg0) {
		
		int modelPoint = viewToModel(new Point(arg0.getX(), arg0.getY()));
		Element map = getDocument().getDefaultRootElement();
        modelPoint= map.getElementIndex(modelPoint);
		System.out.println(lineStat.containsKey(modelPoint) + "," + lineStat.size());
		String rtn = null;
		if (lineStat.containsKey(modelPoint)) {
			CoverageLine li = lineStat.get(modelPoint);
			rtn = li.getBranchCounter().getCovered() + "/"
					+ li.getBranchCounter().getTotal();
		}
		return rtn;
	}

    private Map<Integer, AttributeSet> createStyleMap(CoverageClass clz)
    {
        int base = clz.getFirstLine();
        lineStat = new HashMap<Integer, CoverageLine>();
        Map<CoverageCounterValue, MutableAttributeSet> cache = new HashMap<CoverageCounterValue, MutableAttributeSet>();
        Map<Integer, AttributeSet> lineToStyle = new HashMap<Integer, AttributeSet>();
        for (int i = 0; i < (clz.getLastLine() - base); i++)
        {
			CoverageLine covLine = clz.getLine(i);
            CoverageCounterValue lineStatus = CoverageCounterValue.from(covLine
                .getStatus());
            MutableAttributeSet styleToUse = cache.get(lineStatus);
            if(lineStatus != CoverageCounterValue.EMPTY) {
            	lineStat.put(i, clz.getLine(i));
            }
            if (styleToUse == null)
            {
            	
                styleToUse = new SimpleAttributeSet();
                for(LineAttributes attr : lineAttributes) {
                	attr.setStyle(styleToUse, covLine);
                }
                cache.put(lineStatus, styleToUse);
            }
            lineToStyle.put(base + i - 1, styleToUse);
        }
        return lineToStyle;
    }

}

