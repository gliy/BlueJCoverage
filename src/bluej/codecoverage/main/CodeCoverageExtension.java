package bluej.codecoverage.main;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JToggleButton;

import bluej.codecoverage.CoverageAction;
import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.ui.CoverageMenuBuilder;
import bluej.codecoverage.ui.CoveragePreferences;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.event.PackageEvent;
import bluej.extensions.event.PackageListener;

/**
 * TestAttacherExtension serves as an entrance point for the Extension, allowing
 * BlueJ to load it.
 * 
 * @author Ian Kingsbury
 */
public class CodeCoverageExtension extends Extension {

   /** The Constant NAME. */
   private static final String NAME = "Code Coverage";

   /** The Constant VERSION. */
   private static final String VERSION = "1.0";

   /**
    * When this method is called, the extension may start its work.
    * 
    * @param bluej
    *           the bluej application
    */
   @Override
   public void startup(final BlueJ bluej) {

      try {
         CoveragePrefManager.getPrefs(bluej);
         CoverageUtilities.create(bluej);
         CoverageAction.init(bluej);

         final Map<File, JToggleButton> coverageButtons = new HashMap<File, JToggleButton>();
         bluej.setPreferenceGenerator(new CoveragePreferences(bluej));
         bluej.addPackageListener(new PackageListener() {

            @Override
            public void packageOpened(PackageEvent event) {
               try {
                  CoverageMenuBuilder builder = new CoverageMenuBuilder(bluej,
                        event.getPackage());
                  boolean selected = false;
                  if (!coverageButtons.isEmpty()) {
                     selected = coverageButtons.values().iterator().next()
                           .isSelected();
                  } else {
                     builder.getButton().addItemListener(CoverageAction.get());
                  }
                  coverageButtons.put(event.getPackage().getDir(),
                        builder.getButton());
                  builder.getButton().setSelected(selected);
                  builder.getButton().addItemListener(new ItemListener() {

                     @Override
                     public void itemStateChanged(ItemEvent e) {
                        for (JToggleButton button : coverageButtons.values()) {
                           button.setSelected(e.getStateChange() == ItemEvent.SELECTED);
                        }

                     }
                  });

               } catch (Exception e) {
                  e.printStackTrace();
               }
            }

            @Override
            public void packageClosing(PackageEvent event) {
               try {
                  File dir = event.getPackage().getDir();
                  JToggleButton button = coverageButtons.remove(dir);
                  if (button != null) {
                     for (ItemListener listener : button.getItemListeners()) {
                        button.removeItemListener(listener);
                     }
                     button.getParent().remove(button);
                  }
               } catch (Exception e) {
                  e.printStackTrace();
               }

            }
         });

      } catch (Exception ex) {
         ex.printStackTrace();
      }

   }

   @Override
   public boolean isCompatible() {
      return true;
   }

   @Override
   public String getVersion() {
      return VERSION;
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public String getDescription() {
      return ("Adds Jacoco Code Coverage to BlueJ");
   }

}
