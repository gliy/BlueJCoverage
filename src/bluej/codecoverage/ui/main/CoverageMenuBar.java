package bluej.codecoverage.ui.main;

import java.awt.Component;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import bluej.codecoverage.utils.join.CoverageBundleManager;
import bluej.codecoverage.utils.join.CoverageBundleManager.CoverageBundle;

public class CoverageMenuBar extends JMenuBar {
   private JComboBox<CoverageBundle> previous;
   private JButton sort;
   private JTextField search;
   private CoverageBundleManager bundleManager;
   private CoverageBundle activeBundle;
   private MutableComboBoxModel<CoverageBundle> previousModel;

   @SuppressWarnings("unchecked")
   public CoverageMenuBar(CoverageBundleManager bundleManager,
         CoverageBundle activeBundle) {
      this.bundleManager = bundleManager;
      this.activeBundle = activeBundle;
      this.previousModel = new DefaultComboBoxModel<CoverageBundleManager.CoverageBundle>(
            new Vector<CoverageBundle>(bundleManager.getAllBundles()));
      previous = new JComboBox<CoverageBundle>(previousModel);
      previous.setRenderer(BUNDLE_RENDERER);
      sort = new JButton("sort");
      search = new JTextField();

      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      add(previous);
      add(search);
      add(sort);
   }

   private static final BasicComboBoxRenderer BUNDLE_RENDERER = new BasicComboBoxRenderer() {
      public java.awt.Component getListCellRendererComponent(
            @SuppressWarnings("rawtypes") javax.swing.JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
         super.getListCellRendererComponent(list,
               value, index, isSelected, cellHasFocus);
         setText(((CoverageBundle) value).getName());
         return this;
      };
   };

   public void reset(CoverageBundle activeBundle) {
      previousModel.insertElementAt(activeBundle, 0);
   }
}
