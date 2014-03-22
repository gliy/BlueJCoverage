package bluej.codecoverage.pref.option;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FramePreferences extends BasePreferenceOption<Integer> {

   @SuppressWarnings("unchecked")
   private static final List<Pref<Integer>> OPTIONS = Arrays.asList(
         pref("frame.width", 800), pref("frame.height", 555),
         pref("frame.divider", 450), pref("frame.location.x", (Integer) null),
         pref("frame.location.y", (Integer) null));

   @Override
   public Map<String, Integer> load() {
      Map<String, Integer> frameValues = new LinkedHashMap<String, Integer>();
      for (Pref<Integer> opt : OPTIONS) {
         frameValues.put(opt.key, load(opt));
      }
      return frameValues;
   }

   @Override
   protected void save(Map<String, Integer> values) {
      for (Entry<String, Integer> value : values.entrySet()) {
         bluej.setExtensionPropertyString(value.getKey(), ""
               + value.getValue());
      }
   }

   private Integer load(Pref<Integer> opt) {
      String value = bluej.getExtensionPropertyString(opt.key, null);
      Integer rtn = opt.def;
      if (value != null) {
         rtn = Integer.parseInt(value);
      }
      return rtn;
   }
   @Override
   public boolean isVisible() {
     return false;
   }

}
