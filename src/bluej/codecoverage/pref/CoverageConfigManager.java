package bluej.codecoverage.pref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bluej.codecoverage.ui.ext.DefaultLineAttributes;
import bluej.codecoverage.ui.ext.LineAttributes;
import bluej.codecoverage.ui.ext.LineToolTip;
import bluej.extensions.BlueJ;

public class CoverageConfigManager {
	private static CoverageConfigManager config;
    private BlueJ bluej;
    private static final String FILE_NAME = "coverageprefs.properties";
    private static GUIConfig gui;
    private CoverageConfigManager()
    {
    	gui = new GUIConfig();
    }

    public static CoverageConfigManager getConfig()
    {
        if (config == null)
        {
            config = new CoverageConfigManager();
        }
        return config;
    }

    public void init(BlueJ bluej)
    {
        
        this.bluej = bluej;
    }

    public List<LineAttributes> getAllAttributes() {
    	return Collections.unmodifiableList(gui.attributes);
    }
    public void addAttribut(LineAttributes attr) {
    	gui.attributes.add(attr);
    }
    
    
    private static class GUIConfig {
    	private List<LineAttributes> attributes;
    	private LineToolTip tooltip;
    	private GUIConfig() {
    		attributes = new ArrayList<LineAttributes>();
    		attributes.add(new DefaultLineAttributes());
    	}
    	
    	
    }
}
