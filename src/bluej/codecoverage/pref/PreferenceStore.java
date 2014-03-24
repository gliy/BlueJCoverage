package bluej.codecoverage.pref;

public interface PreferenceStore {

   public String getPreference(String key);
   public String getPreference(String key, String def);
   public void setPreference(String key, String value);
   public String getLocation();
   
   
   
}
