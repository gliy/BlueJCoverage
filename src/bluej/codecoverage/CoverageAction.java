package bluej.codecoverage;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import bluej.codecoverage.ui.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BProject;

/**
 * Performs the attach action when a choice is selected.
 * 
 * @author Ian
 * 
 */
public class CoverageAction extends AbstractAction
{
    private BProject base;
    private Frame location;
    public static final String START = "START";

    public static final String STOP = "END";
  
    public CoverageAction(BProject base, Frame location)
    {
        super();
        this.location = location;
        this.base = base;
    }

    /**
     * When a class is selected for attachment, attempts to attach the class prompting
     * the user if the class is already attached.
     * 
     * @param anEvent
     *            the trigger event
     */
    @Override
    public void actionPerformed(ActionEvent anEvent)
    {
        if(START.equals(anEvent.getActionCommand())) {
            startCoverage();
        } else if(STOP.equals(anEvent.getActionCommand())) {
            endCoverage();
        }
    }

    

    private void endCoverage()
    {
        try
        {
            @SuppressWarnings("unchecked")
            List<CoveragePackage> coverage = CoverageUtilities.get().getResults(base.getDir());
            List<BCoveragePackage> bcoverage = BCoverageBridge.toBCoverage(coverage, base.getDir());
            JFrame report = new CoverageReportFrame(bcoverage);
            report.setLocationRelativeTo(location);
            report.setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    
    }

    private void startCoverage()
    {
        CoverageUtilities utils = CoverageUtilities.get();
        utils.clearResults(); 
    }

}
