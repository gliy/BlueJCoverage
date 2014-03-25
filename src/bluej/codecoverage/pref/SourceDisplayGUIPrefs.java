package bluej.codecoverage.pref;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import bluej.codecoverage.ui.ext.DefaultLineAttributes;
import bluej.codecoverage.ui.ext.DefaultLineToolTip;
import bluej.codecoverage.ui.ext.DefaultSidebarPainter;
import bluej.codecoverage.ui.ext.LineAttributes;
import bluej.codecoverage.ui.ext.LineToolTip;
import bluej.codecoverage.ui.ext.SidebarPainter;

/**
 * Default Preference loader to use when displaying the coverage information.
 * 
 * @author Ian
 * 
 */
public class SourceDisplayGUIPrefs {
   /** Style attribute manager like color for line elements in a source file */
   private LineAttributes attributes;
   /** Tooltip to show when a line is hovered */
   @Getter
   private LineToolTip tooltip;
   /**
    * Sidebar painter for items to show in the gutter where the line numbers are
    * displayed
    */
   @Getter
   private SidebarPainter painter;

   public SourceDisplayGUIPrefs(PreferenceManager manager) {
      attributes = new DefaultLineAttributes(manager.getColorPrefs());
      tooltip = new DefaultLineToolTip();
      painter = new DefaultSidebarPainter();
   }

   public List<LineAttributes> getAttributes() {
      return Arrays.asList(attributes);
   }

}
