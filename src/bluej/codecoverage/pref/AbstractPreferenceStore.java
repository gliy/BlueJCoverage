package bluej.codecoverage.pref;

import java.io.File;

public abstract class AbstractPreferenceStore implements PreferenceStore {
   private String location;

   public AbstractPreferenceStore(String location) {
      super();
      this.location = location;
   }

   public AbstractPreferenceStore(File location) {
      this(location.toString());
   }
   public String getPreference(String key) {
      return getPreference(key,null);
   }
   public abstract String getPreference(String key, String def);
   public abstract void setPreference(String key, String value);
   public String getLocation() {
      return location;
   }
   
   
   
}
