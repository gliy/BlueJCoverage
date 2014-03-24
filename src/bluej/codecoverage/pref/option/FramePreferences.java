package bluej.codecoverage.pref.option;

import java.awt.Point;

import bluej.codecoverage.pref.PreferenceStore;

public class FramePreferences extends BasePreferenceOption<Integer> {

   private static final Pref<Integer> FRAME_LOCATION_Y = pref(
         "frame.location.y", null);
   private static final Pref<Integer> FRAME_DIVIDER = pref("frame.divider", 450);
   private static final Pref<Integer> FRAME_LOCATION_X = pref(
         "frame.location.x", null);
   private static final Pref<Integer> FRAME_WIDTH = pref("frame.width", 800);
   private static final Pref<Integer> FRAME_HEIGHT = pref("frame.height", 555);

   public FramePreferences(PreferenceStore prefStore) {
      super(prefStore, FRAME_DIVIDER, FRAME_HEIGHT, FRAME_LOCATION_X,
            FRAME_LOCATION_Y, FRAME_WIDTH);

   }



   public Integer getWidth() {
      return getValue(FRAME_WIDTH);
   }

   public Integer getHeight() {

      return getValue(FRAME_HEIGHT);
   }

   public Integer getX() {

      return getValue(FRAME_LOCATION_X);
   }

   public Integer getY() {

      return getValue(FRAME_LOCATION_Y);
   }

   public Integer getDividerLocation() {

      return getValue(FRAME_DIVIDER);
   }


   public Point getFrameLocation() {
      Integer xLoc = getValue(FRAME_LOCATION_X);
      Integer yLoc = getValue(FRAME_LOCATION_Y);
      if (xLoc == null || yLoc == null) {
         return null;
      }
      return new Point(xLoc, yLoc);
   }

   public void saveFramePrefs(int width, int height, int div, Point location) {
      save(FRAME_WIDTH, width);
      save(FRAME_HEIGHT, height);
      save(FRAME_LOCATION_X, location.x);
      save(FRAME_LOCATION_Y, location.y);
      save(FRAME_DIVIDER, div);
   }



   @Override
   protected String save(Integer value) {
     return value.toString();
   }



   @Override
   protected Integer load(String value) {
      return Integer.parseInt(value);
   }

}
