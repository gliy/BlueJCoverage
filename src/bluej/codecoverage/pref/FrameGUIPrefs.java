package bluej.codecoverage.pref;

import static java.lang.Integer.parseInt;

import java.awt.Point;

import bluej.extensions.BlueJ;

public class FrameGUIPrefs {
   private static final String FRAME_WIDTH = "frame.width";
   private static final String FRAME_HEIGHT = "frame.height";
   private static final String FRAME_DIVIDER = "frame.divider";

   private static final String FRAME_LOCATION = "frame.location";

   private BlueJ bluej;

   FrameGUIPrefs(BlueJ bluej) {
      this.bluej = bluej;
   }

   public Integer getFrameWidth() {
      return parseInt(bluej.getExtensionPropertyString(FRAME_WIDTH, "800"));
   }

   public Integer getFrameHeight() {
      return parseInt(bluej.getExtensionPropertyString(FRAME_HEIGHT, "550"));
   }

   public Integer getFrameDivider() {
      Integer defaultRtn = (getFrameWidth() / 2) + 30;
      return parseInt(bluej.getExtensionPropertyString(FRAME_DIVIDER, ""
            + defaultRtn));
   }

   public Point getLocation() {
      String point = bluej.getExtensionPropertyString(FRAME_LOCATION, null);
      if (point != null) {
         String[] loc = point.split(",");
         return new Point(parseInt(loc[0]), parseInt(loc[1]));
      }
      return null;
   }

   public void setLocation(Point point) {
      bluej.setExtensionPropertyString(FRAME_LOCATION, point.x + "," + point.y);

   }

   public void setFramePrefs(int frameWidth, int frameHeight, int frameDivider) {
      bluej.setExtensionPropertyString(FRAME_WIDTH, "" + frameWidth);
      bluej.setExtensionPropertyString(FRAME_HEIGHT, "" + frameHeight);
      bluej.setExtensionPropertyString(FRAME_DIVIDER, "" + frameDivider);
   }

}
