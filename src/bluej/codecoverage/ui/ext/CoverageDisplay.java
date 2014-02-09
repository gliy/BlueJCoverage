package bluej.codecoverage.ui.ext;

import javax.swing.JPanel;

import bluej.extensions.BClass;

public interface CoverageDisplay
{

    String getClassName();
    BClass getBClass();
    JPanel getDisplay();
}