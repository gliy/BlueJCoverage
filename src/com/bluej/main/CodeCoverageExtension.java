package com.bluej.main;

import static com.bluej.main.CoverageUtilities.setup;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;

import com.bluej.extension.ui.CoverageMenuBuilder;
import com.bluej.extension.ui.CoveragePreferences;
import com.bluej.prefs.CoveragePrefManager;

/**
 * TestAttacherExtension serves as an entrance point for the 
 * Extension, allowing BlueJ to load it.
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
    public void startup(BlueJ bluej)
    {

        setup(bluej);
        bluej.setPreferenceGenerator(new CoveragePreferences());
        bluej.setMenuGenerator(new CoverageMenuBuilder());
        CoveragePrefManager.getPrefs().init(bluej);
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
