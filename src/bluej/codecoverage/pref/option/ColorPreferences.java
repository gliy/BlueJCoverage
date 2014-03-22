package bluej.codecoverage.pref.option;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ColorPreferences extends BasePreferenceOption<Color> {
   @SuppressWarnings("unchecked")
   private static final List<Pref<Color>> OPTIONS = Arrays.asList(
         pref("Not Covered", new Color(255, 170, 170)),
         pref("Partially Covered", new Color(255, 255, 204)),
         pref("Fully Covered", new Color(204, 255, 204)));

   @Override
   public Map<String, Color> load() {

      Map<String, Color> colors = new LinkedHashMap<String, Color>();
      for (Pref<Color> opt : OPTIONS) {
         colors.put(opt.key, load(opt));
      }
      return colors;
   }

   @Override
   protected void save(Map<String, Color> values) {
      for (Entry<String, Color> value : values.entrySet()) {
         bluej.setExtensionPropertyString(value.getKey(), ""
               + value.getValue().getRGB());
      }
   }

   private Color load(Pref<Color> opt) {
      String value = bluej.getExtensionPropertyString(opt.key, null);
      Color rtn = null;
      if (value != null) {
         rtn = new Color(Integer.parseInt(value));
      } else {
         rtn = opt.def;
      }
      return rtn;
   }
}
