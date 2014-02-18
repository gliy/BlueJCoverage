/*
 * 
 */
package bluej.codecoverage.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private static final String AGENT_FILE_NAME = "jacocoagent.jar";

    /** The number of targets. */
    private static final String PROPERTY_FILE_NAME = "bluej.properties";
    private static final String VM_ARG_KEY = "bluej.vm.args";
    private static final String PORT_NUMBER = "port.number";

    /** Running instance of bluej. */
    private BlueJ bluej;

    private String vmArgsToAdd;
    
    private int port;

    private static CoverageUtilities utils;
    private static CurrentPreferences prefs;
    private Object coverageListener;

    private File propertyFile;
    private File agentFile;

    private static boolean hooked = false;
    /**
     * Instantiates a new test attacher utilities.
     * @throws IOException 
     */
    private CoverageUtilities(BlueJ bluej) throws IOException
    {
        this.bluej = bluej;
        prefs = CoveragePrefManager.getPrefs(bluej).get();
        port = findOpenPort();
        setup();
        setupListener();
       

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
    public List<CoveragePackage> getResults(File file)
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
        findFiles();
        checkHooks();
    }

    private void findFiles()
    {
        agentFile = new File(bluej.getUserConfigDir(), AGENT_FILE_NAME);
        propertyFile = new File(bluej.getUserConfigDir(), PROPERTY_FILE_NAME);
    }

    /**
     * Validates that the bluej properties file has the required VM arguments, and that
     * the agent jarfile is present.
     * 
     * @throws IOException
     */
    private void checkHooks() throws IOException
    {

        Properties props = new Properties();

        props.load(new FileInputStream(propertyFile));

        Object current = props.get(VM_ARG_KEY);
        vmArgsToAdd = buildVMArgs();
        if (!agentFile.exists())
        {
            String err = "jacocoagent.jar was not found at " + agentFile.getAbsolutePath();
            JOptionPane.showMessageDialog(bluej.getCurrentFrame(), err, "BCoverage",
                JOptionPane.ERROR_MESSAGE);

        }
        else if (current == null || !current.toString()
            .contains(vmArgsToAdd))
        {
            JOptionPane.showMessageDialog(
                bluej.getCurrentFrame(),
                "This looks like a setting changed or it is your first time running"
                    + " BCoverage.\nPlease restart bluej for it to take effect.");
           addShutdownHook();
           throw new ExtensionUnloadedException();
        }
    }

    private String buildVMArgs()  {
        final File agent = agentFile;
        String arg= "-javaagent:" + agent.getAbsolutePath()
            + "=output=tcpclient,port=" + port + getExcludes();
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

    /**
     * Attempts to locate a usable port if the default port is not free.
     * @return valid port number to use.
     */
    private int findOpenPort() 
    {
        int tried = 0;
        int newPort = Integer.parseInt(bluej.getExtensionPropertyString(PORT_NUMBER, "6300"));
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
                close(server);
            }
        }
        if (!notFound)
        {
            throw new RuntimeException("No port found");
        }
        bluej.setExtensionPropertyString(PORT_NUMBER, "" + newPort);
        return newPort;

    }

    public void addShutdownHook()
    {
        if (!hooked )
        {
            hooked = true;
            final Properties props = new Properties();
            Runtime.getRuntime()
                .addShutdownHook(new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            props.load(new FileInputStream(propertyFile));
                            Object current = props.get(VM_ARG_KEY);

                            if (current == null || !current.toString()
                                .contains(vmArgsToAdd))
                            {
                                props.put(VM_ARG_KEY, buildVMArgs());
                                props.store(new FileOutputStream(propertyFile),
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

    }

    public static void close(Closeable stream)
    {
        try
        {
            if(stream != null){
            stream.close();
            }
        }
        catch (IOException ex)
        {

        }
    }

}
