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
public class PreferenceManager {
   private PreferenceStore prefStore;
   private ExcludesPreferences excludesPrefs;
   private ColorPreferences colorPrefs;
   private FramePreferences framePrefs;
   public PreferenceManager(CodeCoverageModule module) {
      this.prefStore = module.getPreferenceStore();
      this.excludesPrefs = new ExcludesPreferences(prefStore);
      this.colorPrefs = new ColorPreferences(prefStore);
      this.framePrefs = new FramePreferences(prefStore);
   }

   public PreferenceStore getPrefStore() {
      return prefStore;
   }

   public ExcludesPreferences getExcludesPrefs() {
      return excludesPrefs;
   }

   public ColorPreferences getColorPrefs() {
      return colorPrefs;
   }

   public FramePreferences getFramePrefs() {
      return framePrefs;
   }
 
   public static ImageIcon getImage(Enum<?> type, String ext) {

      URL imageLoc = PreferenceManager.class.getClassLoader().getResource(
            type.toString().toLowerCase() +"." +ext);
      ImageIcon image = null;
      if (imageLoc != null) {
         image = new ImageIcon(imageLoc);
      }
      return image;
   }
}
