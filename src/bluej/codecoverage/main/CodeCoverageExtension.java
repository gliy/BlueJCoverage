package bluej.codecoverage.main;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JToggleButton;

import bluej.codecoverage.CoverageAction;
import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.ui.button.CoverageMenuBuilder;
import bluej.codecoverage.ui.pref.CoveragePreferencePane;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BPackage;
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
   private static URL SITE_URL = null;

   /**
    * When this method is called, the extension may start its work.
    * 
    * @param bluej
    *           the bluej application
    */
   @Override
   public void startup(final BlueJ bluej) {

      try {
         SITE_URL = new URL("http://gliy.github.io/BlueJCoverage");
         final CodeCoverageModule module = new BlueJCodeCoverageModule(bluej);

         final Map<BPackage, CoverageMenuBuilder> coverageButtons = new HashMap<BPackage, CoverageMenuBuilder>();
         bluej.setPreferenceGenerator(new CoveragePreferencePane(module));
         bluej.addPackageListener(new PackageListener() {

            @Override
            public void packageOpened(PackageEvent event) {
               try {
                  CoverageMenuBuilder menuBuilder = new CoverageMenuBuilder(module, event.getPackage());
                  coverageButtons.put(event.getPackage(), menuBuilder);
                  menuBuilder.build();

               } catch (Exception e) {
                  e.printStackTrace();
               }
            }

            @Override
            public void packageClosing(PackageEvent event) {
               try {
                  System.out.println("Closing");
                  CoverageMenuBuilder menuBuilder = coverageButtons.get(event.getPackage());
                  
                  if (menuBuilder != null) {
                     menuBuilder.remove();
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
      return "Adds Jacoco Code Coverage to BlueJ\r\nCreated By Ian Kingsbury\r\nPlugin Idea from Dr.Dalbey";
   }

   @Override
   public URL getURL() {
      return SITE_URL;
   }

}
