package bluej.codecoverage.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.UIManager;

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.ui.CoverageMenuBuilder;
import bluej.codecoverage.ui.CoveragePreferences;
import bluej.codecoverage.utils.BreakoutClassloader;
import bluej.codecoverage.utils.CoverageListener;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.event.ExtensionEvent;
import bluej.extensions.event.ExtensionEventListener;
import bluej.extensions.event.PackageEvent;
import bluej.extensions.event.PackageListener;

/**
 * TestAttacherExtension serves as an entrance point for the Extension, allowing BlueJ to
 * load it.
 * 
 * @author Ian Kingsbury
 */
public class CodeCoverageExtension extends Extension
{

    /** The Constant NAME. */
    private static final String NAME = "Test Attacher";

    /** The Constant VERSION. */
    private static final String VERSION = "1.0";

    /**
     * When this method is called, the extension may start its work.
     * 
     * @param bluej
     *            the bluej application
     */
    @Override
    public void startup(final BlueJ bluej)
    {

        try
        {
            
            CoverageUtilities.create(bluej);
            bluej.setPreferenceGenerator(new CoveragePreferences(bluej));
            bluej.addPackageListener(new PackageListener()
            {
                
                @Override
                public void packageOpened(PackageEvent event)
                {
                    try
                    {
                        new CoverageMenuBuilder(bluej, event.getPackage());
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void packageClosing(PackageEvent event)
                {
                    // TODO Auto-generated method stub
                    
                }
            });
            CoveragePrefManager.getPrefs()
                .init(bluej);
            bluej.addExtensionEventListener(new ExtensionEventListener()
            {

                @Override
                public void eventOccurred(ExtensionEvent event)
                {
                    System.out.println(event.toString());
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        // ClassAttachAction(aClass.getPackage().getProject()).actionPerformed(null);

    }

    @Override
    public boolean isCompatible()
    {
        return true;
    }

    @Override
    public String getVersion()
    {
        return VERSION;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDescription()
    {
        return ("Attaches tests to classes");
    }

}
