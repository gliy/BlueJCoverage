package bluej.codecoverage.pref;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.ImageIcon;

import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BlueJ;

/**
 * Manages any external resources used when displaying the coverage report,
 *  or determining what to coverage information to collect.
 * @author ikingsbu
 *
 */
public class CoveragePrefManager
{
    private static CoveragePrefManager prefs;
    private CurrentPreferences currentPrefs;
    private BlueJ bluej;
    private CoveragePrefManager(BlueJ bluej)
    {
        this.bluej = bluej;
        currentPrefs = new DefaultPreferences();
        
    }

    public static CoveragePrefManager getPrefs(BlueJ bluej) {
        if (prefs == null)
        {
            prefs = new CoveragePrefManager(bluej);
            prefs.load();
        }
        return prefs;
    }
    public static CoveragePrefManager getPrefs()
    {        
        return prefs;
    }

    
    public CurrentPreferences load()
    {

        for (PrefKey key : PrefKey.values())
        {
            currentPrefs.setPref(key, key.type.load(key.name()));
        }
 
        return currentPrefs;
    }

    public void save()
    {
        OutputStream out = null;

        for (Entry<PrefKey, Object> current : currentPrefs.prefs.entrySet())
        {
            bluej.setExtensionPropertyString(current.getKey()
                .name(), current.getKey().type.save(current.getValue()));
        }

    }
    
    public CurrentPreferences get()
    {
        return currentPrefs;
    }


    private static class StaticPreferences {
        public final ImageIcon getPackageIcon() {
            return getImage("package.png");
        }
        public final ImageIcon getSourceIcon() {
            return getImage("source.png");
        }
        
        private ImageIcon getImage(String name) {
            URL imageLoc = getClass().getClassLoader().getResource(name);
            ImageIcon image = null;
            if(imageLoc != null) {
                image = new ImageIcon(imageLoc);
            }
            return image;
        }
    }
    public static class CurrentPreferences extends StaticPreferences
    {
        private Map<PrefKey, Object> prefs = new EnumMap<PrefKey, Object>(PrefKey.class);
        protected List<String> excluded;
        private CurrentPreferences(Map<PrefKey, Object> defaults)
        {
            this.excluded = new ArrayList<String>();
            this.prefs = defaults;
        }

        public void setPref(PrefKey key, Object val)
        {
            if (val != null)
            {
                prefs.put(key, val);
            }
        }
        
        @SuppressWarnings("unchecked")
        public <E> E getPref(PrefKey key) {
            return (E) prefs.get(key);
        }
        public List<String> getExcluded() {
            return excluded;
        }
    }

    private static class DefaultPreferences extends CurrentPreferences
    {
        
        private DefaultPreferences()
        {
            super(loadDefaultPrefs());
            loadDefaults();
        }
        private static Map<PrefKey, Object> loadDefaultPrefs() {
            Map<PrefKey, Object> prefs = new EnumMap<PrefKey, Object>(PrefKey.class);
            prefs.put(PrefKey.NOT_COVERED_COLOR, Color.RED);
            prefs.put(PrefKey.PARTIALLY_COVERED_COLOR, Color.YELLOW);
            prefs.put(PrefKey.FULLY_COVERED_COLOR, Color.GREEN);
            return prefs;
        }
        
        private void loadDefaults()
        {
            Properties props = new Properties();
            try {
                props.load(getClass().getClassLoader().getResourceAsStream("defaultprefs.properties"));
                String[] excludes = props.get("excludes").toString().split(":");
                excluded.addAll(Arrays.asList(excludes));
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void addExclude(String name) {
            excluded.add(name);
        }
    }

    public enum PrefKey
    {
        NOT_COVERED_COLOR("Not Covered", COLOR_TYPE), PARTIALLY_COVERED_COLOR(
            "Partially Covered", COLOR_TYPE), FULLY_COVERED_COLOR(
            "Fully Covered", COLOR_TYPE);
        private String display;
        private Saveable type;

        private PrefKey(String display, Saveable type)
        {
            this.display = display;
            this.type = type;
        }

        public String getDisplay()
        {
            return display;
        }
    }
    private interface Saveable<E> {
        String save(E value);
        E load(String key);
    }
    private final static Saveable<Color> COLOR_TYPE = new Saveable<Color>() {

        @Override
        public String save(Color value)
        {
            return "" + value.getRGB();
        }

        @Override
        public Color load(String key)
        {
            String value = getPrefs().bluej.getExtensionPropertyString(key, null);
            Color rtn = null;
            if(value != null){
                rtn = new Color(Integer.parseInt(value));
            }
            return rtn;
        }
        
    };
}
