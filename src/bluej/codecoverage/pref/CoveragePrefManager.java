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
import java.util.Properties;

import javax.swing.ImageIcon;

import bluej.codecoverage.utils.CoverageUtilities;

/**
 * Manages any external resources used when displaying the coverage report,
 *  or determining what to coverage information to collect.
 * @author ikingsbu
 *
 */
public class CoveragePrefManager
{
    private static CoveragePrefManager prefs;
    private static final String FILE_NAME = "coverageprefs.properties";
    private CurrentPreferences currentPrefs;
    private CoveragePrefManager()
    {
        currentPrefs = new DefaultPreferences();
    }

    public static CoveragePrefManager getPrefs()
    {
        if (prefs == null)
        {
            prefs = new CoveragePrefManager();
        }
        return prefs;
    }


    public CurrentPreferences load()
    {
        InputStream in = null;
        try{
            Properties currentPrefsFile = new Properties();
            in = getClass().getClassLoader().getResourceAsStream("current.properties");
            currentPrefsFile.load(in);
        for (PrefKey key : PrefKey.values())
        {
            currentPrefs.addPref(key,
                currentPrefsFile.get(key.toString()));
        }
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            CoverageUtilities.close(in);
        }
        return currentPrefs;
    }

    public void save()
    {
        OutputStream out = null;
        try
        {
            Properties currentPrefsFile = new Properties();

            for (PrefKey key : PrefKey.values())
            {
                currentPrefsFile.put(key.toString(), currentPrefs.getPref(key));
            }
            out = new FileOutputStream(new File(getClass().getClassLoader()
                .getResource("current.properties").getFile()));
            currentPrefsFile.store(out, "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            CoverageUtilities.close(out);
        }
    }
    public CurrentPreferences get()
    {
        return currentPrefs;
    }

    public void setPref(PrefKey key) {
        
    }

    private static class StaticPreferences {
        public ImageIcon getPackageIcon() {
            return getImage("package.png");
        }
        public ImageIcon getSourceIcon() {
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

        public void addPref(PrefKey key, Object val)
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
            prefs.put(PrefKey.TOTALLY_COVERED_COLOR, Color.GREEN);
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
        NOT_COVERED_COLOR("Not Covered"), PARTIALLY_COVERED_COLOR("Partially Covered"), TOTALLY_COVERED_COLOR(
            "Fully Covered");
        private String display;

        private PrefKey(String display)
        {
            this.display = display;
        }

        public String getDisplay()
        {
            return display;
        }
    }
}
