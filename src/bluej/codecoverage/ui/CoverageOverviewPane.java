package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.utils.join.BCoverage;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoverageCounter;
import bluej.codecoverage.utils.serial.CoverageType;

/**
 * Displays an overview of the code coverage for a class in a tree structure.
 * Allows the user to click on an item in the tree to open an editor with the
 * source code.
 * 
 * @author ikingsbu
 * 
 */
class CoverageOverviewPane extends JPanel {

   private CurrentPreferences prefs;
   private DefaultTreeModel model;
   private JTree tree;
   private JPanel summary;
   private TreeSelectionListener summarySelection = new TreeSelectionListener() {

      @Override
      public void valueChanged(TreeSelectionEvent e) {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
               .getLastSelectedPathComponent();
         if (node != null) {
            buildSummary((BCoverage<?>) node.getUserObject());
         }
      }
   };

   public CoverageOverviewPane(List<BCoveragePackage> coverage,
         CurrentPreferences prefs) {
      super();
      this.prefs = prefs;
      this.summary = new JPanel();
      summary.setBorder(BorderFactory.createEtchedBorder());
      summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
      model = new DefaultTreeModel(createRootNode(coverage));
      tree = new JTree(model);
      UIManager.put("ProgressBar.foreground", new Color(122, 215, 122)); // green
      UIManager.put("ProgressBar.selectionForeground", Color.BLACK);

      tree.setRootVisible(false);
      tree.setRowHeight(20);

      tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.setToggleClickCount(1);
      tree.setCellRenderer(renderer);
      tree.addTreeSelectionListener(summarySelection);

      setLayout(new BorderLayout());
      add(new JScrollPane(tree), BorderLayout.CENTER);
      add(summary, BorderLayout.SOUTH);
   }

   public void reset(List<BCoveragePackage> coverage) {
      TreeSelectionListener[] listeners = tree
            .getListeners(TreeSelectionListener.class);
      for (TreeSelectionListener listener : listeners) {
         tree.removeTreeSelectionListener(listener);
      }
      // tree.removeTreeSelectionListener(summarySelection);
      model.setRoot(createRootNode(coverage));
      // tree.addTreeSelectionListener(summarySelection);
      for (TreeSelectionListener listener : listeners) {
         tree.addTreeSelectionListener(listener);
      }
   }

   private void buildSummary(BCoverage<?> selected) {
      JLabel[] summaryLabels = new JLabel[3];
      summaryLabels[0] = new JLabel(printCoverage("Class Coverage: ",
            selected.getClassCoverage()));
      summaryLabels[1] = new JLabel(printCoverage("Branch Coverage: ",
            selected.getBranchCoverage()));
      summaryLabels[2] = new JLabel(printCoverage("Line Coverage: ",
            selected.getLineCoverage()));
      summary.removeAll();
      for (JLabel summaryLabel : summaryLabels) {
         summaryLabel.setFont(summaryLabel.getFont().deriveFont(13f));
         summary.add(summaryLabel);
         summary.add(Box.createVerticalStrut(10));
      }
      summary.revalidate();
      summary.repaint();
   }

   private String printCoverage(String base, CoverageCounter counter) {

      return base + counter.getCovered() + "/"
            + (counter.getCovered() + counter.getMissed());
   }

   private static MutableTreeNode createRootNode(List<BCoveragePackage> coverage) {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("All");
      Collections.sort(coverage, BCoverageBridge.SORT_BY_COVERAGE);
      for (BCoveragePackage pack : coverage) {
         root.add(createNode(pack));
      }
      return root;
   }

   private static DefaultMutableTreeNode createNode(BCoverage<?> pack) {
      DefaultMutableTreeNode packNode = new DefaultMutableTreeNode(pack);

      for (BCoverage<?> clz : pack.getNodes()) {
         packNode.add(createNode(clz));
      }

      return packNode;
   }

   private ImageIcon getDisplayIcon(BCoverage<?> coverage) {
     

      return  CoverageType.findType(coverage.getSource()).getImage();
   }

   public void addListener(TreeSelectionListener listen) {
      tree.addTreeSelectionListener(listen);
   }

   public DefaultMutableTreeNode getSelectedNode() {
      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
            .getLastSelectedPathComponent();
      return selectedNode;
   }

   DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
         Component defaultDisplay = super.getTreeCellRendererComponent(tree,
               value, selected, expanded, leaf, row, hasFocus);
         Object treeNode = ((DefaultMutableTreeNode) value).getUserObject();

         if (treeNode instanceof BCoverage<?>) {

            JPanel rtn = new JPanel();
            BCoverage<?> info = (BCoverage<?>) treeNode;

            ImageIcon iconToUse = getDisplayIcon(info);
            if (iconToUse != null) {
               setIcon(iconToUse);
            }
            setText(info.getName());

            CoverageCounter counter = info.getLineCoverage();
            JProgressBar progress = new JProgressBar();
            progress.setBorderPainted(true);
            progress.setPreferredSize(new Dimension((int) progress
                  .getPreferredSize().getWidth(), this.getHeight()));
            progress.setValue((int) (counter.getCoveredRatio() * 100));

            progress.setStringPainted(true);
            rtn.add(this);
            rtn.add(progress);
            rtn.setBackground(Color.WHITE);
            return rtn;
         } else {
            return defaultDisplay;
         }

      }
   };
}