package com.bluej.extension;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import bluej.extensions.BProject;
import bluej.extensions.ExtensionException;


/**
 * Performs the attach action when a choice is selected.
 * 
 * @author Ian
 * 
 */
public class CoverageAction extends AbstractAction
{



    private BProject project;
    private ClassLoader loader;
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
    public CoverageAction(Object project)
    {
        super("run");

       // this.project = (BProject)project;
    }

    /**
     * When a class is selected for attachment, attempts to attach the class
     * prompting the user if the class is already attached.
     * 
     * @param anEvent the trigger event
     */
    @Override
    public void actionPerformed(ActionEvent anEvent)
    {
        try{
           //new CoverageTutorial(System.out).execute();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            ex.printStackTrace();
        }

    }

}
