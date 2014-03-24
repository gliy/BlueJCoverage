package bluej.codecoverage.pref.option;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ColorPreferences extends BasePreferenceOption<Color> {
   private static final Pref<Color> NOT_COVERED = pref("Not Covered",
         new Color(255, 170, 170));
   private static final Pref<Color> PARTIALLY_COVERED = pref(
         "Partially Covered", new Color(255, 255, 204));
   private static final Pref<Color> FULLY_COVERED = pref("Fully Covered",
         new Color(204, 255, 204));

   @SuppressWarnings("unchecked")
   public ColorPreferences(PreferenceStore prefStore) {
      super(prefStore, NOT_COVERED, PARTIALLY_COVERED, FULLY_COVERED);

   }
   
   public Color getNotCovered() {
      return getValue(NOT_COVERED);
   }

   public Color getPartiallyCovered() {
      return getValue(PARTIALLY_COVERED);
   }

   public Color getFullyCovered() {
      return getValue(FULLY_COVERED);
   }

   @Override
   protected String save(Color value) {
     return "" + value.getRGB();
   }

   @Override
   protected Color load(String key) {
      return new Color(Integer.parseInt(key));
   }

   public void saveColorPrefs(Color notCovered, Color partiallyCovered, Color fullyCovered) {
      save(NOT_COVERED, notCovered);
      save(PARTIALLY_COVERED, partiallyCovered);
      save(FULLY_COVERED, fullyCovered);
   }
}
