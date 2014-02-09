package bluej.codecoverage.ui.ext;

import javax.swing.JPanel;

import bluej.extensions.BClass;

public class DefaultCoverageDisplay implements CoverageDisplay
{
    protected CoverageDisplay display;
    
    public DefaultCoverageDisplay(CoverageDisplay display) {
        this.display = display;
    }
    
    @Override
    public String getClassName()
    {
        return display.getClassName();
    }

    @Override
    public BClass getBClass()
    {
       return display.getBClass();
    }

    @Override
    public JPanel getDisplay()
    {
        return new JPanel();
    }

}
