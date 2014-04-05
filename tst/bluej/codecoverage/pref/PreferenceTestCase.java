package bluej.codecoverage.pref;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public abstract class PreferenceTestCase extends TestCase {
   protected MockPreferenceStore createMockStore() {
      return new MockPreferenceStore();
   }
   public static class MockPreferenceStore extends AbstractPreferenceStore {
      private Map<String, String> prefStore = new HashMap<String, String>();
      public MockPreferenceStore() {
         super("mock");
      }
      
      @Override
      public String getPreference(String key, String def) {
         String val = prefStore.get(key);
         return val != null ? val : def;
      }

      @Override
      public void setPreference(String key, String value) {
         prefStore.put(key, value);
      }
      
      public Map<String, String> getAllProps() {
         return prefStore;
      }
      
   }
}
