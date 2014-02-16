package bluej.codecoverage;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import bluej.codecoverage.ui.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.join.BCoverageBridge;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BlueJ;

/**
 * Performs the attach action when a choice is selected.
 * 
 * @author Ian
 * 
 */
public class CoverageAction implements ItemListener
{

    private BlueJ bluej;
    private static CoverageAction action;
    private CoverageAction(BlueJ bluej)
    {
        super();
        this.bluej = bluej;
        
    }
    

    private void endCoverage()
    {
        try
        {
            Frame location = bluej.getCurrentFrame();
            File dir = bluej.getCurrentPackage().getProject().getDir();
            List<CoveragePackage> coverage = CoverageUtilities.get().getResults(dir);
            List<BCoveragePackage> bcoverage = BCoverageBridge.toBCoverage(coverage, dir);
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

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        boolean selected = e.getStateChange() == ItemEvent.SELECTED;
        if(selected ) {
            startCoverage();
        } else  {
            endCoverage();
        }
        
    }


    public static void init(BlueJ bluej) {
        action = new CoverageAction(bluej);
    }
    public static ItemListener get()
    {
        return action;
    }

}
