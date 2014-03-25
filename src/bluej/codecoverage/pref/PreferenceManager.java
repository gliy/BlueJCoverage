package bluej.codecoverage.pref;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import lombok.Getter;
import bluej.codecoverage.main.CodeCoverageModule;
import bluej.codecoverage.pref.option.ColorPreferences;
import bluej.codecoverage.pref.option.ExcludesPreferences;
import bluej.codecoverage.pref.option.FramePreferences;
import bluej.codecoverage.utils.serial.CoverageType;
import bluej.extensions.BlueJ;

/**
 * Manages any external resources used when displaying the coverage report, or
 * determining what to coverage information to collect.
 * 
 * @author ikingsbu
 * 
 */
@Getter
public class PreferenceManager {
   /** The underlying storage for preferences */
   private PreferenceStore prefStore;
   /** List of packages to ignore when gathering coverage information */
   private ExcludesPreferences excludesPrefs;
   /** Colors to use when displaying coverage results */
   private ColorPreferences colorPrefs;
   /** Frame size and location to use when displaying the coverage frame */
   private FramePreferences framePrefs;

   /**
    * Constructs a new PreferenceManager using the PreferenceStore defined in
    * the specified CodeCoverageModule
    * 
    * @param module
    *           module containing the preference store to use.
    */
   public PreferenceManager(CodeCoverageModule module) {
      this.prefStore = module.getPreferenceStore();
      this.excludesPrefs = new ExcludesPreferences(prefStore);
      this.colorPrefs = new ColorPreferences(prefStore);
      this.framePrefs = new FramePreferences(prefStore);
   }

   /**
    * Loads an image whose name is specified in an enum type.
    * <p>
    * The Enum's toString().toLowerCase() must match the image name on the file
    * system.
    * 
    * @param type
    *           enum representing the name of the image to load.
    * @param ext
    *           the file extension (ex png)
    * @return The image whose name matches the enum, or null if no image is
    *         found.
    */
   public static ImageIcon getImage(Enum<?> type, String ext) {

      URL imageLoc = PreferenceManager.class.getClassLoader().getResource(
            type.toString().toLowerCase() + "." + ext);
      ImageIcon image = null;
      if (imageLoc != null) {
         image = new ImageIcon(imageLoc);
      }
      return image;
   }
}
