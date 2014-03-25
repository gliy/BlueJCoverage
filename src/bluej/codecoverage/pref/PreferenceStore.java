package bluej.codecoverage.pref;

/**
 * Represents a class that controls the persistence of User preferences.
 * <p>
 * An example of this would be {@link bluej.extensions.BlueJ} with
 * getExtensionProperty and setExtensionProperty.</br>
 * 
 * Abstracting BlueJ's preference store allows for easier unit testing by just
 * redirecting the preference store.
 * 
 * @author Ian
 * 
 */
public interface PreferenceStore {

   /**
    * Looks up a preference using the provided key.
    * <p>
    * 
    * @param key
    *           key of the value to lookup
    * @return String value identified by the key, or null if no value is found.
    */
   public String getPreference(String key);

   /**
    * Looks up a preference using the provided key.
    * <p>
    * If value is found the key, then def is returned instead.
    * 
    * @param key
    *           key of the value to look up.
    * @param def
    *           default value to use if no value is found for the key.
    * @return String value identified by the key.
    */
   public String getPreference(String key, String def);

   /**
    * Sets the preference identified by the specified key to the specified
    * value.
    * <p>
    * This overwrites any existing preference stored using the same key.
    * 
    * @param key
    *           key to identify the preference
    * @param value
    *           value of the preference to save
    */
   public void setPreference(String key, String value);

   /**
    * Location where the preference is stored.
    * <p>
    * This is expected to be an absolute path to the directory under which the
    * preferences are stored.
    * 
    * @return absolute path to the director of the preferences.
    */
   public String getLocation();

}
