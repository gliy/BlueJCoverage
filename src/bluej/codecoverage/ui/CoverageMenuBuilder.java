package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import bluej.codecoverage.CoverageAction;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageLine;
import bluej.extensions.BClass;
import bluej.extensions.ExtensionException;
import bluej.extensions.MenuGenerator;

/**
 * Generates an item on a Class's popup menu when that class is right clicked.
 * 
 * @author Ian
 * 
 */
public class CoverageMenuBuilder extends MenuGenerator
{

    /**
     * Generates the menu for attachment if there are valid targets.
     * 
     * @param aClass
     *            the class the popupmenu is for
     * @return the generated menu or null if there are no targets
     * @see bluej.extensions.MenuGenerator#getClassMenuItem(bluej.extensions.BClass)
     */
    @Override
    public JMenuItem getClassMenuItem(final BClass aClass)
    {

       return new JMenuItem(new CoverageAction(aClass));
    }

}
