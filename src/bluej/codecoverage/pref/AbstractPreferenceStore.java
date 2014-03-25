package bluej.codecoverage.pref;

import java.io.File;

/**
 * Abstract preference store that provides default implementations for some of
 * the methods in PreferenceStore.
 * 
 * @author Ian
 * 
 */
public abstract class AbstractPreferenceStore implements PreferenceStore {
   /** file location of the directory where the preference file is located */
   private String location;

   /**
    * Constructs a PreferenceStore with the specified location.
    * 
    * @param location
    *           location of the directory where the preference file is located
    */
   public AbstractPreferenceStore(String location) {
      super();
      this.location = location;
   }

   /**
    * @see #AbstractPreferenceStore(String)
    * @param location
    */
   public AbstractPreferenceStore(File location) {
      this(location.toString());
   }

   /**
    * {@inheritDoc}
    */
   public String getPreference(String key) {
      return getPreference(key, null);
   }

   /**
    * {@inheritDoc}
    */
   public abstract String getPreference(String key, String def);

   /**
    * {@inheritDoc}
    */
   public abstract void setPreference(String key, String value);

   /**
    * {@inheritDoc}
    */
   public String getLocation() {
      return location;
   }

}
