package bluej.codecoverage.pref.option;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcludesPreferences extends BasePreferenceOption<List<String>> {
   private static final Pref<List<String>> EXCLUDES = pref("coverage.excludes",
         Arrays.asList("bluej/runtime**/*.class", "**/*__SHELL*"));

   public ExcludesPreferences(PreferenceStore prefStore) {
      super(prefStore);

   }

   public List<String> getExcludedPrefs() {
      return getValue(EXCLUDES);
   }

   public void saveExcludedPrefs(List<String> excluded) {
      save(EXCLUDES, excluded);
   }

   @Override
   protected String save(List<String> value) {
      String rtn = "";
      for (String exclude : value) {
         rtn += exclude + ",";
      }
      return rtn.substring(0, rtn.length() - 1);
   }

   @Override
   protected List<String> load(String value) {
      return Arrays.asList(value.split(","));
   }


}
