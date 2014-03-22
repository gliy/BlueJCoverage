package bluej.codecoverage.ui.button;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JToggleButton;
import javax.swing.JToggleButton.ToggleButtonModel;

import bluej.codecoverage.CoverageAction;
import bluej.extensions.BlueJ;

public class CoverageButtonFactory {

   private static CoverageButtonFactory inst;

   private CoverageButtonFactory(BlueJ bluej) {
      model.addItemListener(new CoverageAction(bluej));
   }

   public static CoverageButtonFactory get(BlueJ bluej) {
      if (inst == null) {
         inst = new CoverageButtonFactory(bluej);
      }
      return inst;
   }

   public JToggleButton createButton() {
      final JToggleButton button = new JToggleButton("");
      button.setModel(model);
      ItemListener itemListener = new ItemListener() {
         public void itemStateChanged(ItemEvent event) {
            button.setText(getText(event.getStateChange()==ItemEvent.SELECTED));
         }
      };

      button.addItemListener(itemListener);
      button.setText(getText(button.isSelected()));
      return button;
   }

   private ToggleButtonModel model = new ToggleButtonModel() {

      public void setSelected(boolean selected) {
         super.setSelected(selected);
      }
   };

   private static String getText(boolean selected) {
      String prefix = "Start";
      if (selected) {
         prefix = "  End";
      }
      return prefix + " Coverage";
   }

}