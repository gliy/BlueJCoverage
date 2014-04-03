package bluej.codecoverage.pref;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bluej.codecoverage.pref.PreferenceTestCase.MockPreferenceStore;
import bluej.codecoverage.pref.option.BasePreferenceOption.Pref;
import bluej.codecoverage.pref.option.ColorPreferences;
import bluej.codecoverage.pref.option.ExcludesPreferences;
import bluej.codecoverage.pref.option.FramePreferences;

public class PreferenceStoreTest extends PreferenceTestCase {

   private MockPreferenceStore prefs;

   @Override
   public void setUp() {
      prefs = createMockStore();
   }

   public void testColorPrefs() throws Exception {
      ColorPreferences colorPrefs = new ColorPreferences(prefs);
      Iterator<Pref<Color>> prefIterator = colorPrefs.getAllOptions()
               .iterator();
      Map<String, Color> expected = new HashMap<String, Color>();
      while (prefIterator.hasNext()) {
         Pref<Color> pref = prefIterator.next();
         Color newC = pref.def.brighter();
         colorPrefs.save(pref, newC);
         expected.put(pref.key, newC);
      }
      for (Pref<Color> actual : colorPrefs.getAllOptions()) {
         assertEquals(expected.get(actual.key), colorPrefs.getValue(actual));
      }
      
      colorPrefs.saveColorPrefs(Color.BLACK, Color.RED, Color.GREEN);
      assertEquals(Color.BLACK, colorPrefs.getNotCovered());
      assertEquals(Color.RED, colorPrefs.getPartiallyCovered());
      assertEquals(Color.GREEN, colorPrefs.getFullyCovered());
   }
   
   public void testExcludesPrefs() throws Exception {
      ExcludesPreferences excludesPrefs = new ExcludesPreferences(prefs);
      assertEquals(Arrays.asList("bluej/runtime**/*.class", "**/*__SHELL*"), excludesPrefs.getExcludedPrefs());
      excludesPrefs.saveExcludedPrefs(Arrays.asList("A","C"));
      assertEquals(Arrays.asList("A","C"), excludesPrefs.getExcludedPrefs());    
   }
   public void testFramePrefs() throws Exception {
      FramePreferences framePrefs = new FramePreferences(prefs);
      assertEquals((Integer)800, framePrefs.getWidth());
      assertEquals((Integer)555, framePrefs.getHeight());
      assertNull(framePrefs.getFrameLocation());
      Point loc = new Point(4,5);
      framePrefs.saveFramePrefs(1, 2, 3, loc );
      assertEquals((Integer)1, framePrefs.getWidth());
      assertEquals((Integer)2, framePrefs.getHeight());
      assertEquals((Integer)3, framePrefs.getDividerLocation());
      assertEquals(loc, framePrefs.getFrameLocation());
      assertEquals((Integer)loc.x, framePrefs.getX());
      assertEquals((Integer)loc.y, framePrefs.getY());         
   }
}
