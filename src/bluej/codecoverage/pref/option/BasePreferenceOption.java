package bluej.codecoverage.pref.option;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import bluej.codecoverage.main.CodeCoverageExtension;
import bluej.extensions.BlueJ;

public abstract class BasePreferenceOption<E> {
   protected Map<String, E> options;
   protected  BlueJ bluej;
   protected BasePreferenceOption() {
      options = load();
      bluej = CodeCoverageExtension.getBlueJ();
   }

   public abstract Map<String, E> load();

   protected abstract void save(Map<String, E> values);

   public E getValue(String key) {
      return options.get(key);
   }

   public Set<Entry<String, E>> getAllOptions() {
      return options.entrySet();
   }

   public boolean isVisible() {
      return true;
   }
   
   protected static <E> Pref<E> pref(String key, E defaultValue) {
      return new Pref<E>(key, defaultValue);
   }
   
   @AllArgsConstructor
   @EqualsAndHashCode
   public static class Pref<E> {
      final String key;
      final E def;
      
      
   }
}
