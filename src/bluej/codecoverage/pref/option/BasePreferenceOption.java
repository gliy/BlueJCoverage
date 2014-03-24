package bluej.codecoverage.pref.option;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import bluej.codecoverage.pref.PreferenceStore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

public abstract class BasePreferenceOption<E> {
   protected PreferenceStore prefStore;
   private List<Pref<E>> prefKeys;

   protected BasePreferenceOption(PreferenceStore prefStore,
         Pref<E>... prefKeys) {
      this.prefStore = prefStore;
      this.prefKeys = Arrays.asList(prefKeys);

   }

   protected abstract String save(E value);

   protected abstract E load(String value);

   public E getValue(Pref<E> key) {

      String unParsedValue = prefStore.getPreference(key.key);
      E value = null;
      if (unParsedValue != null) {
         value = load(unParsedValue);
      }
      if (value == null) {
         value = key.def;
      }
      return value;
   }

   public final void save(Pref<E> key, E value) {
      if (value != null && key != null) {
         prefStore.setPreference(key.key, save(value));
      }
   }

   public Collection<Pref<E>> getAllOptions() {
      return prefKeys;
   }

   protected static <E> Pref<E> pref(String key, E defaultValue) {
      return new Pref<E>(key, defaultValue);
   }

   @AllArgsConstructor
   @EqualsAndHashCode
   public static class Pref<E> {
      public final String key;
      public final E def;

   }
}
