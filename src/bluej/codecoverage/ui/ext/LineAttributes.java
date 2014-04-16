package bluej.codecoverage.ui.ext;

import javax.swing.text.MutableAttributeSet;

import bluej.codecoverage.utils.serial.CoverageLine;

/**
 * Represents any special attributes to show for a line in the source code.
 * @author ikingsbu
 *
 */
public interface LineAttributes {
   /**
    * Registers style information for a specific line of coverage information.
    * @param style attribute collector
    * @param line coverage information
    */
   void setStyle(MutableAttributeSet style, CoverageLine line);

}
