package bluej.codecoverage.ui.main;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;
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

import bluej.codecoverage.main.CodeCoverageModule;
import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.option.FramePreferences;
import bluej.codecoverage.utils.join.BCoverage;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.join.ClassInfo;
import bluej.codecoverage.utils.join.CoverageBundleManager;
import bluej.codecoverage.utils.join.CoverageBundleManager.CoverageBundle;
import bluej.codecoverage.utils.join.Locatable;

/**
 * Main display for the results of the code coverage.
 * 
 * @author Ian
 */
public class CoverageReportFrame extends JFrame implements Saveable {
   /** The coverage information to show in the overview. */
   private CoverageBundle bundle;
   /** The open source file tabs */
   private JTabbedPane tabs;
   /** The overview panel */
   private CoverageOverviewPane overview;
   /** Map of opened source files */
   private Map<String, CoverageSourceDisplay> classToDisplay;
   /** GUI Preferences */
   private PreferenceManager prefManager;
   /** Preferences for frame location and size */
   private FramePreferences framePrefs;
   /** Singleton for the entire frame */
   private boolean first;
   /** Previous location of this frame */
   private Frame location;
   /** Divider between overview and source code. */
   private JSplitPane split;
   /** Previous Coverage run manager */
   private CoverageBundleManager bundleManager;
   private CoverageMenuBar menuBar;
   
   public CoverageReportFrame(CodeCoverageModule module) {
      prefManager = module.getPreferenceManager();
      framePrefs = prefManager.getFramePrefs();
      first = true;
      this.bundleManager = module.getBundleManager();
   }
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
    * @throws Exception
    */
   public void create(
         CoverageBundle bundle, Frame location)
         throws Exception {
      // if it is the first time opening actually create the frame.
      if (first) {
         first = false;
         createNewFrame(bundle, location);
      } else {
         // otherwise just reset the information and make it visible
         reset(bundle);
      }
   }

   /**
    * Creates a new coverage frame.
    * 
    * @see #create(List, Frame)
    * @param bundle
    *           coverage information to show
    * @param fr
    *           frame location and size
    * @throws Exception
    */
   private void createNewFrame(CoverageBundle bundle, Frame fr)
         throws Exception {
      this.location = fr;
      this.bundle = bundle;
      
      setSize(framePrefs.getWidth(), framePrefs.getHeight());
      addWindowListener(frameSizeSaver);

      generateTabs();
      setTitle("Coverage Report");
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      menuBar = new CoverageMenuBar(bundleManager, bundle);
      setJMenuBar(menuBar);
      Point loc = framePrefs.getFrameLocation();
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
    * @param bundle
    *           new information to show.
    */
   private void reset(CoverageBundle bundle) {
      this.bundle = bundle;
      classToDisplay.clear();
      tabs.removeAll();
      List<BCoveragePackage> allPackages = bundle.getAllPackages();
      overview.reset(allPackages);
      menuBar.reset(bundle);
   }

   /**
    * Sets up the divider location, and adds
    * the overview and source display to the frame.
    */
   private void generateTabs() {
      classToDisplay = new HashMap<String, CoverageSourceDisplay>();
      tabs = new JTabbedPane();
      List<BCoveragePackage> allPackages = bundle.getAllPackages();
      overview = new CoverageOverviewPane(allPackages);
      bundleManager.addSaver(overview);
      bundleManager.addSaver(this);
      overview.setPreferredSize(new Dimension(getWidth(), 100));

      split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, overview);

      setContentPane(split);

      split.setDividerLocation(framePrefs.getDividerLocation());
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
                  prefManager, clz);
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
            .moveCaret(selectedCoverage.getFirstLine(), selectedCoverage.getLastLine());

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
         framePrefs.saveFramePrefs(width, height, div, getLocation());

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

   private static final String OPEN_TABS = "tabs.open";
   
   @Override
   public void save(CoverageBundle state) {
      state.getBundleState().save(OPEN_TABS, new ArrayList<String>(classToDisplay.keySet()));
      classToDisplay.clear();
      tabs.removeAll();
      
   }
   @Override
   public void load(CoverageBundle state) {
      List<String> openTabs = (List<String>) state.getBundleState().load(OPEN_TABS);
      Map<String, BCoverageClass> tabsToOpen = new HashMap<String, BCoverageClass>();
      
      for (BCoveragePackage pkg : state.getAllPackages()) {
         findTabs(tabsToOpen, openTabs, pkg);
      }
      for (String tabId : openTabs) {
         bringUpTab(tabsToOpen.get(tabId));
      }
   }
   private void findTabs(Map<String, BCoverageClass> into, List<String> open, BCoverage<?> node) {
      if(open.contains(node.getId()) && node instanceof BCoverageClass) {
         into.put(node.getId(), (BCoverageClass)node);
      }
      for (BCoverage<?> child : node.getNodes()) {
         findTabs(into, open, child);
      }
   }

}
