/*
 * 
 */
package bluej.codecoverage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BlueJ;
import bluej.extensions.ExtensionUnloadedException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

/**
 * Utiltiy Class that controls all interactions that might be needed by multiple classes.
 * This includes setting up the jacocoagent, reading the properties file,
 * and gathering the results from the agent.
 * 
 * @author Ian
 */
public final class CoverageUtilities
{

    /** Standard Java package separator. */
    private static final String PACKAGE_SEP = ".";

    /** The number of targets. */
    private static final String TARGET_COUNT = "package.numTargets";

    /** package properties file name. */
    private static final String OLD_PKG = "bluej.pkg";

    /** Running instance of bluej. */
    private BlueJ bluej;

    private String vmArgsToAdd;
    private static final String VM_ARG_KEY = "bluej.vm.args";
    private int port = 6300;

    private static CoverageUtilities utils;
    private static CurrentPreferences prefs;
    private Object coverageListener;

    /**
     * Instantiates a new test attacher utilities.
     * @throws IOException 
     */
    private CoverageUtilities(BlueJ bluej) throws IOException
    {
        this.bluej = bluej;
        prefs = CoveragePrefManager.getPrefs().loadDefault();
        setupListener();
        setup();
       

    }

    private void setupListener()
    {
        try
        {
            coverageListener = new BreakoutClassloader(bluej.getUserConfigDir()).loadClass(
                CoverageListener.class.getName())
                .getConstructor(Integer.TYPE).newInstance(port);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static CoverageUtilities create(BlueJ blueJ) throws IOException
    {
        if (utils != null)
        {
            throw new RuntimeException("Utils class already created");
        }
        utils = new CoverageUtilities(blueJ);
        return utils;
    }

    public static CoverageUtilities get()
    {
        if (utils == null)
        {
            throw new RuntimeException("Utils class not created");
        }
        return utils;
    }

    public void clearResults() {
        try{
            Method clearMethod = coverageListener.getClass()
                .getDeclaredMethod("clearResults");
            clearMethod.invoke(coverageListener);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public Collection<CoveragePackage> getResults(File file)
    {
        ObjectInputStream input = null;
        List<CoveragePackage> rtn = new ArrayList<CoveragePackage>();
        try
        {
            Method resultsMethod = coverageListener.getClass()
                .getDeclaredMethod("getResults", File.class);
            input = (ObjectInputStream) resultsMethod.invoke(coverageListener,
                file);
            Object readIn;
            while ((readIn = input.readObject()) != null)
            {
                rtn.add((CoveragePackage)readIn);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return rtn;
    }

    /**
     * Sets up the Utilites class so it can properly manage associations.
     * 
     * @param bluejApp
     *            the running blueJ application
     * @throws IOException
     */
    public void setup() throws IOException
    {

        checkHooks();

    }

    private void checkHooks() throws IOException
    {
        checkHooks(getPropsFile());
    }

    private void checkHooks(File propsFile) throws IOException
    {
        System.out.println(System.getProperty("java.class.path"));
        Properties props = new Properties();

        props.load(new FileInputStream(propsFile));

        Object current = props.get(VM_ARG_KEY);
        vmArgsToAdd = buildVMArgs();
        if (current == null || !current.toString()
            .contains(vmArgsToAdd))
        {
            JOptionPane.showMessageDialog(
                bluej.getCurrentFrame(),
                "This looks like it is your first time running"
                    + "\nthe code coverage extension. Please restart bluej for it to take effect.");
            addShutdownHook();
           
            throw new ExtensionUnloadedException();
        }

    }
    private String buildVMArgs() {
        final File agent = new File(bluej.getUserConfigDir() + File.separator + "jacocoagent.jar");
        int newPort = 6300;
        String arg= "-javaagent:" + agent.getAbsolutePath()
            + "=output=tcpclient,port=" + newPort + getExcludes();
        port = newPort;
        return arg;
    }
    private String getExcludes() {
        StringBuilder buildExcludes = new StringBuilder();
        for(String ex : prefs.getExcluded()) {
            buildExcludes.append(":" + ex);
        }
        String rtn = buildExcludes.toString();
        if(!rtn.isEmpty()) {
            rtn = ",excludes=" + rtn.substring(1);
            System.out.println("excludes: " + rtn);
        }
        return rtn;
    }

    private int findOpenPort() 
    {
        int tried = 0;
        int newPort = port;
        boolean notFound = true;
        ServerSocket server = null;
        while (tried < 10 && notFound)
        {
            try
            {
                tried++;
                server = new ServerSocket(newPort);
                notFound = server.isBound();
            }catch(ConnectException ex) {
                break;
            }
            catch (Exception ex)
            {
                newPort++;
                ex.printStackTrace();
                System.out.println("Trying to connect to port " + newPort);
            } finally {
                try
                {
                    if(server != null) {
                        server.close();
                    }
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (!notFound)
        {
            throw new RuntimeException("No port found");
        }
        return newPort;

    }

    public void addShutdownHook()
    {

        final Properties props = new Properties();
        final File propsFile = getPropsFile();
        final File agent = new File(bluej.getUserConfigDir() + File.separator + "jacocoagent.jar");
        Runtime.getRuntime()
            .addShutdownHook(new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    try
                    {
                        props.load(new FileInputStream(propsFile));
                        Object current = props.get(VM_ARG_KEY);
                        System.out.println(agent.exists());
                        if (current == null || !current.toString()
                            .contains(vmArgsToAdd))
                        {
                            String toAdd = "";
                            if (current != null)
                            {
                               // toAdd = current + " ";
                            }
                            toAdd += "-javaagent:" + agent.getAbsolutePath()
                                + "=output=tcpclient,port=" + port + getExcludes();
                            props.put(VM_ARG_KEY, toAdd);
                            props.store(new FileOutputStream(propsFile),
                                "");

                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                    }
                }
            }));

    }

    private File getPropsFile()
    {
        File libs = bluej.getUserConfigDir();
        for (File child : libs.listFiles())
        {
            if (child.getName()
                .equals("bluej.properties"))
            {
                return child;
            }
        }
        return null;
    }

    /**
     * Gets the file.
     * 
     * @return the file
     * @throws ProjectNotOpenException
     *             the project not open exception
     * @throws PackageNotFoundException
     *             the package not found exception
     */
    private File getFile() throws ProjectNotOpenException,
        PackageNotFoundException
    {
        return new File(bluej.getCurrentPackage()
            .getDir() + File.separator + OLD_PKG);
    }

    public List<ClassLocation> getPackageClassLocations()
    {
        Properties props = new Properties();
        List<ClassLocation> locs = new ArrayList<ClassLocation>();
        InputStream fis = null;
        try
        {
            fis = new FileInputStream(getFile());
            props.load(fis);
            int targetCount = Integer.parseInt(props.getProperty(TARGET_COUNT));
            for (int index = 1; index <= targetCount; index++)
            {
                String name = props.getProperty(Keys.NAME.make(index));
                Integer x = Integer.parseInt(props.getProperty(Keys.X.make(index)));
                Integer y = Integer.parseInt(props.getProperty(Keys.Y.make(index)));
                locs.add(new ClassLocation(name, x, y));
            }
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
        return locs;

    }

    /**
     * Keys that represent the information we care about for classes'
     */
    private enum Keys
    {
        NAME, X, Y;

        /**
         * Creates a key as represented in the properties file.
         * 
         * @param index
         *            the index of the target
         * @return the properties string
         */
        public String make(int index)
        {
            return "target" + index + "." + name().toLowerCase();
        }
    }

    public static class ClassLocation
    {
        private String name;
        private int x;
        private int y;

        public ClassLocation(String name, int x, int y)
        {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        private String getName()
        {
            return name;
        }

        private void setName(String name)
        {
            this.name = name;
        }

        private int getX()
        {
            return x;
        }

        private void setX(int x)
        {
            this.x = x;
        }

        private int getY()
        {
            return y;
        }

        private void setY(int y)
        {
            this.y = y;
        }

    }

}
