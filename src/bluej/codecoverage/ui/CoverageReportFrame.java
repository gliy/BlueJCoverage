package bluej.codecoverage.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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

import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.PreferenceManager.CurrentPreferences;
import bluej.codecoverage.pref.FrameGUIPrefs;
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
   /** The coverage information to show in the overview. */
   private List<BCoveragePackage> coverage;
   /** The open source file tabs */
   private JTabbedPane tabs;
   /** The overview panel */
   private CoverageOverviewPane overview;
   /** Map of opened source files */
   private Map<String, CoverageSourceDisplay> classToDisplay;
   /** GUI Preferences */
   private CurrentPreferences prefs = PreferenceManager.getPrefs().get();
   /** Preferences for frame location and size */
   private FrameGUIPrefs framePrefs;
   /** Singleton for the entire frame */
   private static CoverageReportFrame instance;
   /** Previous location of this frame */
   private Frame location;
   /** Divider between overview and source code. */
   private JSplitPane split;

   /**
    * Creates a coverage frame for the provided coverage information.
    * <p>
    * If a coverage frame already exists, resets it show the new coverage
    * information.
    * 
    * @param classesCovered
    *           coverage information to display
    * @param location
    *           location and size for the coverage frame if this is the first
    *           time opening it
    * @return the new coverage frame.
    * @throws Exception
    */
   public static CoverageReportFrame create(
         List<BCoveragePackage> classesCovered, Frame location)
         throws Exception {
      // if it is the first time opening actually create the frame.
      if (instance == null) {
         instance = new CoverageReportFrame(classesCovered, location);
      } else {
         // otherwise just reset the information and make it visible
         instance.reset(classesCovered);
      }

      return instance;
   }

   /**
    * Creates a new coverage frame.
    * 
    * @see #create(List, Frame)
    * @param classesCovered
    *           coverage information to show
    * @param fr
    *           frame location and size
    * @throws Exception
    */
   private CoverageReportFrame(List<BCoveragePackage> classesCovered, Frame fr)
         throws Exception {
      this.location = fr;
      this.coverage = classesCovered;
      this.framePrefs = prefs.getFramePrefs();
      setSize(framePrefs.getFrameWidth(), framePrefs.getFrameHeight());
      addWindowListener(frameSizeSaver);

      generateTabs();
      setTitle("Coverage Report");
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      Point loc = framePrefs.getLocation();
      if (loc != null) {
         setLocation(loc);
      } else {
         setLocationRelativeTo(location);
      }
      location = this;
   }

   /**
    * Resets the frame to show the new coverage information.
    * 
    * @param newInfo
    *           new information to show.
    */
   private void reset(List<BCoveragePackage> newInfo) {
      this.coverage = newInfo;
      classToDisplay.clear();
      tabs.removeAll();

      overview.reset(newInfo);
   }

   /**
    * Sets up the divider location, and adds
    * the overview and source display to the frame.
    */
   private void generateTabs() {
      classToDisplay = new HashMap<String, CoverageSourceDisplay>();
      tabs = new JTabbedPane();
      overview = new CoverageOverviewPane(coverage, prefs);
      overview.setPreferredSize(new Dimension(getWidth(), 100));

      split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, overview);

      setContentPane(split);

      split.setDividerLocation(framePrefs.getFrameDivider());
      split.setDividerSize(8);
      split.setOneTouchExpandable(true);
      overview.addListener(new TreeListener());
   }

   /**
    * Brings up the source code when a node is clicked on the overview panel.
    * 
    * @param clz selected class.
    */
   private void bringUpTab(BCoverageClass clz) {
      try {
         ClassInfo bclass = clz.getClassInfo();
         CoverageSourceDisplay existingDisplay = classToDisplay.get(bclass
               .getId());
         // if the source code is not already open
         if (existingDisplay == null) {
            final CoverageSourceDisplay newDisplay = new CoverageSourceDisplay(
                  clz);
            classToDisplay.put(newDisplay.getId(), newDisplay);
            // add listener so Ctrl+W will remove the tab for the source code
            newDisplay.getSource().addKeyListener(new KeyAdapter() {

               @Override
               public void keyPressed(KeyEvent e) {
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

   /**
    * Moves the caret on the source code display when an element is selected in the overview panel.
    * <p>
    * An example is when a method is selected on the overview panel, it moves the viewport of the source code to that method.
    * @param node selected element on the overview panel
    * @param parent parent node to open if the source is not current open.
    */
   private void moveCaret(DefaultMutableTreeNode node,
         DefaultMutableTreeNode parent) {
      // either open the parent or make it the active tab
      bringUpTab((BCoverageClass) parent.getUserObject());
      Locatable selectedCoverage = (Locatable) node.getUserObject();

      ((CoverageSourceDisplay) tabs.getSelectedComponent())
            .moveCaret(selectedCoverage.getFirstLine());

   }

   /**
    * Saves the frame information like size and location when it is closed.
    */
   private WindowListener frameSizeSaver = new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {

         int width = getWidth();
         int height = getHeight();
         int div = split.getDividerLocation();
         framePrefs.setFramePrefs(width, height, div);
         framePrefs.setLocation(getLocation());
         super.windowClosed(e);
      }
   };

   /**
    * Listens for when a node is selected in the overview panel.
    * @author Ian
    *
    */
   class TreeListener implements TreeSelectionListener {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
         DefaultMutableTreeNode node = overview.getSelectedNode();
         BCoverage<?> selectedCoverage = (BCoverage<?>) node.getUserObject();
         // packages are the only node that cant open a source code file
         if (!(selectedCoverage instanceof BCoveragePackage)) {
            DefaultMutableTreeNode parent = node;
            // if it is a method open its source file
            if (((DefaultMutableTreeNode) node.getParent()).getUserObject() instanceof BCoverageClass) {
               parent = (DefaultMutableTreeNode) node.getParent();
            }
            moveCaret(node, parent);
         }

      }
   }

}
