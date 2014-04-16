package bluej.codecoverage.ui.ext;

import java.awt.Graphics;

import bluej.codecoverage.utils.serial.CoverageLine;

/**
 * Represents a painter who can paint in the gutter that shows line numbers.
 * @author ikingsbu
 *
 */
public interface SidebarPainter {
   /**
    * Called when a repaint is needed for the gutter.
    * @param g graphics object to use
    * @param row line number covered on
    * @param x x cord to start drawing
    * @param y y cord to start drawing
    * @param width width of the space
    * @param height height of the space
    */
   void paint(Graphics g, int row, int x, int y, int width, int height);
   /**
    * Registers coverage information for a line so the correct text can be shown.
    * @param line line number the coverage information is for
    * @param data coverage information for that line
    */
   void registerLine(int line, CoverageLine data);

}
