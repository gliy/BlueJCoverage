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
import bluej.codecoverage.utils.serial.CoverageType;
import bluej.extensions.BlueJ;

/**
 * Manages any external resources used when displaying the coverage report, or
 * determining what to coverage information to collect.
 * 
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

    public static CoveragePrefManager getPrefs(BlueJ bluej)
    {
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
            Object loadedValue = key.type.load(key.name());
            if (loadedValue != null)
            {
                currentPrefs.setPref(key, loadedValue);
            }
        }

        return currentPrefs;
    }

    public void save()
    {
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

    private static class StaticPreferences
    {
       

        public ImageIcon getImage(CoverageType type)
        {
            
            URL imageLoc = getClass().getClassLoader()
                .getResource(type.toString().toLowerCase() + ".png");
            ImageIcon image = null;
            if (imageLoc != null)
            {
                image = new ImageIcon(imageLoc);
            }
            return image;
        }
    }

    public static class CurrentPreferences extends StaticPreferences
    {
        private Map<PrefKey, Object> prefs = new EnumMap<PrefKey, Object>(
            PrefKey.class);
     

        private CurrentPreferences(Map<PrefKey, Object> defaults)
        {
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
        public <E> E getPref(PrefKey key)
        {
            return (E) prefs.get(key);
        }

        public List<String> getExcluded()
        {
            return getPref(PrefKey.EXCLUDED);
        }

        public void addExcluded(String name)
        {
            List<String> excluded = getExcluded();
            excluded.add(name);
            setPref(PrefKey.EXCLUDED, excluded);
        }

        public void removeExcluded(int index)
        {
            List<String> excluded = getExcluded();
            excluded.remove(index);
            setPref(PrefKey.EXCLUDED, excluded);
        }

       

    }

    private static class DefaultPreferences extends CurrentPreferences
    {
        public static final String[] DEFAULT_EXCLUDES = new String[]{"bluej/runtime**/*.class","**/*__SHELL*"};

        private DefaultPreferences()
        {
            super(loadDefaultPrefs());
        }

        private static Map<PrefKey, Object> loadDefaultPrefs()
        {
            Map<PrefKey, Object> prefs = new EnumMap<PrefKey, Object>(
                PrefKey.class);
            prefs.put(PrefKey.NOT_COVERED_COLOR, new Color(255,170,170));
            prefs.put(PrefKey.PARTIALLY_COVERED_COLOR, new Color(255,255,204));
            prefs.put(PrefKey.FULLY_COVERED_COLOR, new Color(204,255,204));
            prefs.put(PrefKey.EXCLUDED, Arrays.asList(DEFAULT_EXCLUDES));
            return prefs;
        }
    }

    @SuppressWarnings("rawtypes")
    public enum PrefKey
    {
        NOT_COVERED_COLOR("Not Covered", COLOR_TYPE), PARTIALLY_COVERED_COLOR(
            "Partially Covered", COLOR_TYPE), FULLY_COVERED_COLOR(
            "Fully Covered", COLOR_TYPE),
        EXCLUDED(null, LIST_TYPE);

        private String display;
        private Storable type;

        private PrefKey(String display, Storable type)
        {
            this.display = display;
            this.type = type;
        }

        public String getDisplay()
        {
            return display;
        }
    }

    private interface Storable<E>
    {
        String save(E value);

        E load(String key);
    }

    private final static Storable<Color> COLOR_TYPE = new Storable<Color>()
    {

        @Override
        public String save(Color value)
        {
            return "" + value.getRGB();
        }

        @Override
        public Color load(String key)
        {
            String value = getPrefs().bluej.getExtensionPropertyString(key,
                null);
            Color rtn = null;
            if (value != null)
            {
                rtn = new Color(Integer.parseInt(value));
            }
            return rtn;
        }

    };
    private final static Storable<List<String>> LIST_TYPE = new Storable<List<String>>()
    {

        @Override
        public String save(List<String> value)
        {
            if(value == null || value.isEmpty()) {
                value = Arrays.asList(DefaultPreferences.DEFAULT_EXCLUDES);
            }
            String save = "";
            for(String toSave : value) {
                save += toSave + "\n";
            }
            return save.trim();
        }

        @Override
        public List<String> load(String key)
        {
            String value = getPrefs().bluej.getExtensionPropertyString(key,
                null);
            List<String> rtn = null;
            if(value != null && value.length() > 0) {
                rtn = new ArrayList<String>(Arrays.asList(value.split("\n")));
            }
            
            return rtn;
        }

    };
}
