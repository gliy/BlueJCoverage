package bluej.codecoverage;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import bluej.codecoverage.ui.CoverageReportFrame;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.ExtensionException;

/**
 * Performs the attach action when a choice is selected.
 * 
 * @author Ian
 * 
 */
public class CoverageAction extends AbstractAction implements Observer
{

    private Action wrap;
    private BClass bClass;
    private BlueJ bluej;

    /**
     * Constructs the action to be executed when the menu item is pressed. Sets the text
     * for the action in the menu to be the class name of the unit test provided.
     * 
     * @param source
     *            class that the menu appeared on
     * @param unitTestClass
     *            class that should be attached to source
     * 
     * @throws ExtensionException
     *             if there is an error getting the editor throws an ExtensionException
     */
    public CoverageAction(Action wrap, String label, BClass bClass, BlueJ bluej)
    {
        super(label);
        this.wrap = wrap;
        this.bClass = bClass;
        this.bluej = bluej;
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
        CoverageUtilities utils = CoverageUtilities.get();
        utils.clearResults();
        wrap.actionPerformed(anEvent);
        CoverageInvokationListener.setup(bClass, bluej)
            .addObserver(this);

    }

    @Override
    public void update(Observable o, Object arg)
    {
        try{
        @SuppressWarnings("unchecked")
        List<CoverageClass> coverage = (List<CoverageClass>) arg;
        Map<BClass, CoverageClass> bclassToCoverage = new HashMap<BClass, CoverageClass>();
        BProject[] allProjects = bluej.getOpenProjects();
        for(BProject project : allProjects) {
            for (CoverageClass coverageClass : coverage)
            {
                BPackage bpack = project.getPackage(coverageClass.getPackageName());
                if(bpack != null) {
                    BClass foundClass = bpack.getBClass(coverageClass.getName());
                    if(foundClass != null) {
                        bclassToCoverage.put(foundClass, coverageClass);
                    }
                }
            }
        }
        JFrame report = new CoverageReportFrame(bclassToCoverage);
        report.setLocationRelativeTo(bluej.getCurrentFrame());
        
        report.setVisible(true);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
