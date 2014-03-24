package bluej.codecoverage.pref.option;

public interface PreferenceStore {

   public String getPreference(String key);
   public String getPreference(String key, String def);
   public String setPreference(String key, String value);
   public String getLocation();
   
   
   
}
