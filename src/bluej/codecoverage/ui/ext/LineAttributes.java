package bluej.codecoverage.ui.ext;

import javax.swing.text.MutableAttributeSet;

import bluej.codecoverage.utils.serial.CoverageLine;

public interface LineAttributes {

	void setStyle(MutableAttributeSet style, CoverageLine line);
	
}
