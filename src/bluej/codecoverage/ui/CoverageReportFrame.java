package bluej.codecoverage.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.utils.join.BCoverage;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.join.ClassInfo;
import bluej.codecoverage.utils.join.Locatable;

/**
 * Main display for the results of the code coverage.
 * 
 * @author Ian
 */
public class CoverageReportFrame extends JFrame {
   private List<BCoveragePackage> coverage;
   private JTabbedPane tabs;
   private CoverageOverviewPane overview;
   private Map<String, CoverageSourceDisplay> classToDisplay;
   private CurrentPreferences prefs = CoveragePrefManager.getPrefs().get();
   private static CoverageReportFrame instance;
   private Frame location;

   public static CoverageReportFrame create(
         List<BCoveragePackage> classesCovered, Frame location)
         throws Exception {
      if (instance == null) {
         instance = new CoverageReportFrame(classesCovered, location);
      } else {

         instance.reset(classesCovered);
      }

      return instance;
   }

   private CoverageReportFrame(List<BCoveragePackage> classesCovered, Frame fr)
         throws Exception {
      this.location = fr;

      this.coverage = classesCovered;
      setSize(700, 500);
      generateTabs();
      setTitle("Coverage Report");
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      setLocationRelativeTo(location);
      location = this;
   }

   private void reset(List<BCoveragePackage> newInfo) {
      this.coverage = newInfo;
      classToDisplay.clear();
      tabs.removeAll();

      CoverageOverviewPane overviewTmp = new CoverageOverviewPane(coverage,
            prefs);
      overviewTmp.setPreferredSize(new Dimension(overview.getWidth(), 100));
      overviewTmp.addListener(new TreeListener());
      overview = overviewTmp;

      JSplitPane split = ((JSplitPane) getContentPane());

      split.setRightComponent(overview);
      split.setDividerLocation(split.getDividerLocation());
      setLocationRelativeTo(location);
      location = this;
   }

   private void generateTabs() {
      classToDisplay = new HashMap<String, CoverageSourceDisplay>();
      tabs = new JTabbedPane();
      // add(tabs, BorderLayout.CENTER);
      overview = new CoverageOverviewPane(coverage, prefs);
      overview.setPreferredSize(new Dimension(getWidth(), 100));

      JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs,
            overview);

      setContentPane(split);
      split.setDividerLocation((getWidth() / 2) + 30);
      split.setDividerSize(8);
      split.setOneTouchExpandable(true);
      overview.addListener(new TreeListener());
   }

   private void bringUpTab(BCoverageClass clz) {
      try {
         ClassInfo bclass = clz.getClassInfo();
         CoverageSourceDisplay existingDisplay = classToDisplay.get(bclass
               .getId());
         if (existingDisplay == null) {
            final CoverageSourceDisplay newDisplay = new CoverageSourceDisplay(
                  clz);
            classToDisplay.put(newDisplay.getId(), newDisplay);
            newDisplay.getSource().addKeyListener(new KeyAdapter() {

               @Override
               public void keyPressed(KeyEvent e) {
                  System.out.println(e);
                  if (e.getKeyCode() == KeyEvent.VK_W && e.isControlDown()) {
                     CoverageSourceDisplay selected = (CoverageSourceDisplay) tabs
                           .getSelectedComponent();
                     tabs.remove(selected);
                     classToDisplay.remove(selected.getId());
                  }
               }
            });
            tabs.add(newDisplay, bclass.getName());
            existingDisplay = newDisplay;
         }
         tabs.setSelectedComponent(existingDisplay);

      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   private void moveCaret(DefaultMutableTreeNode node,
         DefaultMutableTreeNode parent) {
      bringUpTab((BCoverageClass) parent.getUserObject());
      Locatable selectedCoverage = (Locatable) node.getUserObject();

      ((CoverageSourceDisplay) tabs.getSelectedComponent())
            .moveCaret(selectedCoverage.getFirstLine());

   }

   class TreeListener implements TreeSelectionListener {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
         DefaultMutableTreeNode node = overview.getSelectedNode();
         BCoverage<?> selectedCoverage = (BCoverage<?>) node.getUserObject();
         if (!(selectedCoverage instanceof BCoveragePackage)) {
            DefaultMutableTreeNode parent = node;
            if (((DefaultMutableTreeNode) node.getParent()).getUserObject() instanceof BCoverageClass) {
               parent = (DefaultMutableTreeNode) node.getParent();
            }
            moveCaret(node, parent);
            // BCoverageClass bClassInfo = (BCoverageClass) selectedCoverage;
            // bringUpTab(bClassInfo);
         }

      }
   }

}
