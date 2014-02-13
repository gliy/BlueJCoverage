package bluej.codecoverage.ui.ext;

import java.awt.Color;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;

public class DefaultLineAttributes implements LineAttributes {
	@Override
	public void setStyle(MutableAttributeSet style, CoverageLine line) {
		CoverageCounterValue value = CoverageCounterValue
				.from(line.getStatus());
		switch (value) {
		case FULLY_COVERED:
			StyleConstants.setBackground(style, Color.green);
			break;
		case NOT_COVERED:
			StyleConstants.setBackground(style, Color.RED);
			break;
		case EMPTY:
			break;
		case PARTLY_COVERED:
			StyleConstants.setBackground(style, Color.YELLOW);
			break;
		case UNKNOWN:
			break;
		}
	}
}
