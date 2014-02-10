package bluej.codecoverage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.text.Element;

import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageCounterValue;
import bluej.codecoverage.utils.serial.CoverageLine;
import bluej.extensions.BClass;
import bluej.extensions.BlueJ;
import bluej.extensions.ExtensionException;
import bluej.extensions.event.InvocationEvent;
import bluej.extensions.event.InvocationListener;

/**
 * Performs the attach action when a choice is selected.
 * 
 * @author Ian
 * 
 */
public class CoverageAction extends AbstractAction 
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
        CoverageInvokationListener.setup(bClass, bluej);
       
    }

   

   

}
