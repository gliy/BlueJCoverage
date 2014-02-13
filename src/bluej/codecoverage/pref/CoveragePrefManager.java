package bluej.codecoverage.pref;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;

import bluej.codecoverage.utils.CoverageUtilities.ClassLocation;
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
    private BlueJ bluej;
    private static final String FILE_NAME = "coverageprefs.properties";

    private CoveragePrefManager()
    {

    }

    public static CoveragePrefManager getPrefs()
    {
        if (prefs == null)
        {
            prefs = new CoveragePrefManager();
        }
        return prefs;
    }

    public void init(BlueJ bluej)
    {
        
        this.bluej = bluej;
    }

    public CurrentPreferences loadDefault()
    {
        /*File configDir = bluej.getUserConfigDir();
        for (File inDir : configDir.listFiles())
        {
            if (inDir.getName()
                .equals(FILE_NAME))
            {
                return loadFromFile(inDir);
            }
        }*/
        return new DefaultPreferences();
    }

    private CurrentPreferences loadFromFile(File file)
    {
        Properties props = new Properties();

        List<ClassLocation> locs = new ArrayList<ClassLocation>();
        InputStream fis = null;
        CurrentPreferences loadedPrefs = null;
        try
        {
            fis = new FileInputStream(file);
            props.load(fis);

           // String name = props.getProperty(PrefKey.NAME.make(index));
         //   Integer x = Integer.parseInt(props.getProperty(PrefKey.X.make(index)));
         //   Integer y = Integer.parseInt(props.getProperty(PrefKey.Y.make(index)));
          //  locs.add(new ClassLocation(name, x, y));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (loadedPrefs == null)
        {
            return new DefaultPreferences();
        }
        return loadedPrefs;
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
        private Color notCovered;
        private Color paritallyCovered;
        private Color totallyCovered;
        protected List<String> excluded;
        private CurrentPreferences(Color notCovered, Color paritallyCovered,
            Color totallyCovered)
        {
            this.notCovered = notCovered;
            this.paritallyCovered = paritallyCovered;
            this.totallyCovered = totallyCovered;
            this.excluded = new ArrayList<String>();
        }

        private Color getNotCovered()
        {
            return notCovered;
        }

        private Color getParitallyCovered()
        {
            return paritallyCovered;
        }

        private Color getTotallyCovered()
        {
            return totallyCovered;
        }
        public List<String> getExcluded() {
            return excluded;
        }
    }

    private static class DefaultPreferences extends CurrentPreferences
    {
        
        private DefaultPreferences()
        {
            super(Color.RED, Color.YELLOW, Color.GREEN);
            loadDefaults();
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

    private enum PrefKey
    {
        NOT_COVERED, PARTIALLY_COVERED, TOTALLY_COVERED;
    }
}
