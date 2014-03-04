package bluej.codecoverage.pref;

import java.util.Arrays;
import java.util.List;

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

   private LineAttributes attributes;
   private LineToolTip tooltip;
   private SidebarPainter painter;
   public SourceDisplayGUIPrefs() {
      attributes = new DefaultLineAttributes();
      tooltip = new DefaultLineToolTip();
      painter = new DefaultSidebarPainter();
   }

   public List<LineAttributes> getAttributes() {
      return Arrays.asList(attributes);
   }

   public LineToolTip getTooltip() {
      return tooltip;
   }

   public SidebarPainter getPainter() {
      return painter;
   }
   

}
