package bluej.codecoverage.utils;

import java.awt.Color;
import java.awt.Desktop;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import bluej.codecoverage.main.CodeCoverageModule;
import bluej.codecoverage.utils.listener.CoverageAgent;
import bluej.codecoverage.utils.serial.CoveragePackage;
import bluej.extensions.BlueJ;
import bluej.extensions.ExtensionUnloadedException;

/**
 * Utiltiy Class that controls all interactions that might be needed by multiple
 * classes. This includes setting up the jacocoagent, reading the properties
 * file, and gathering the results from the agent.
 * 
 * @author Ian
 */
public final class CoverageUtilities {

   private static final int START_PORT = 6000;

   private static final int END_PORT = 6010;

   /** Standard Java package separator. */
   private static final String AGENT_FILE_NAME = "jacocoagent.jar";

   /** The number of targets. */
   private static final String PROPERTY_FILE_NAME = "bluej.properties";
   /** Key BlueJ uses for vm arguments */
   private static final String VM_ARG_KEY = "bluej.vm.args";
   /** Key we store the port number under */
   private static final String PORT_NUMBER = "port.number";
   /** URL for help dialog */
   private static final String HELP_URL = "https://github.com/gliy/BlueJCoverage/wiki/Help";
   /** VM Args to be appended under {@link #VM_ARG_KEY} */
   private String vmArgsToAdd;
   /** Current port that we use */
   private int port;
   /** Module to load information from. */
   private CodeCoverageModule module;
   /**
    * Jacoco Agent {@link CoverageAgent}.
    * <p>
    * Created by a different ClassLoader, so must be Object.
    */
   private Object coverageListener;

   /** File to write our properties to on shutdown */
   private File propertyFile;
   /** Location of the Jacocoagent.jar file */
   private File agentFile;
   /** Boolean if we have already registered a shutdown hook */
   private static boolean hooked = false;

   /**
    * Instantiates a new test attacher utilities, loads any saved preferences,
    * and sets up the required listeners to collect coverage data.
    * 
    * @param module
    *           module instance to save and load information from.
    * 
    * @throws IOException
    *            if any error is encountered while loading saved preferences,
    *            throws an {@link IOException}.
    */
   public CoverageUtilities(CodeCoverageModule module) throws IOException {
      this.module = module;
      port = Integer.parseInt(module.getPreferenceStore().getPreference(PORT_NUMBER, ""
               + START_PORT));
      port = Math.max(START_PORT, port);
      setup();

      setupListener();

   }

   /**
    * Sets up the the CoverageListener that records Coverage data from the
    * client.
    * <p>
    * This requires the use of {@link BreakoutClassLoader} due to the inability
    * to load any classes outside of your extension by BlueJ's
    * {@link BreakoutClassLoader}.
    */
   private void setupListener() {
      try {
         coverageListener = new BreakoutClassLoader()
                  .loadClass(CoverageAgent.class.getName())
                  .getConstructor(Integer.TYPE).newInstance(port);
      } catch (Exception e) {
         addShutdownHook();
         e.printStackTrace();
      }
   }

  
 

   /**
    * Requests a reset of all collected coverage information so far.
    * <p>
    * Used when you want to start a fresh run.
    */
   public void clearResults() {
      try {
         Method clearMethod = coverageListener.getClass().getDeclaredMethod(
                  "clearResults");
         clearMethod.invoke(coverageListener);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Requests a dump of all collected coverage information for all classes
    * located under file. If any error is encountered while reading the coverage
    * information, an error message is displayed, and null is returned.
    * 
    * @param file
    *           The base directory for finding classes to return coverage for.
    * @return List of code coverage methods for each package found in the file
    *         directory.
    */
   public List<CoveragePackage> getResults(File file) {
      ObjectInputStream input = null;
      List<CoveragePackage> rtn = new ArrayList<CoveragePackage>();
      try {
         // different class loader, have to serialize
         Method resultsMethod = coverageListener.getClass().getDeclaredMethod(
                  "getResults", File.class);
         input = (ObjectInputStream) resultsMethod.invoke(coverageListener,
                  file);
         Object readIn;
         while ((readIn = input.readObject()) != null) {
            rtn.add((CoveragePackage) readIn);
         }
      } catch (Exception e) {
         JOptionPane.showMessageDialog(module.getBlueJFrame(),
                  buildErrorMessage(getRootCause(e).getMessage()),
                  "Code Coverage Error", JOptionPane.ERROR_MESSAGE);
         return null;
      } finally {
         close(input);
      }
      return rtn;
   }

   /**
    * Generates a generic error message to display.
    * 
    * @param ex
    *           the error to show.
    * @return a TextPane that can be added to a dialog for error reporting.
    */
   private JComponent buildErrorMessage(String ex) {
      // "An error was encountered while gathering coverage data\n"
      // getRootCause(e).getMessage()
      JTextPane error = new JTextPane();
      error.setContentType("text/html");
      error.setEditable(false);
      error.setText(String
               .format("An error was encountered while gathering coverage data <br> "
                        + "<span style=\"color:red;font-size:14px;font-family:monospace\">%s</span><br>"
                        + "<a href=%s>Visit the FAQ for more information</a>",
                        ex, HELP_URL));
      error.setBackground(new Color(240, 240, 240));
      error.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e) {
            // open it the URL if it is clicked
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
               if (Desktop.isDesktopSupported()) {
                  try {
                     Desktop.getDesktop().browse(e.getURL().toURI());
                  } catch (Exception e1) {
                  }
               }
            }
         }
      });
      return error;
   }

   /**
    * Finds the required files for loading preferences, and running the
    * listener, and verifies everything exists and is valid.
    * 
    * @throws IOException
    *            If any error is encountered while reading the properties,
    *            throws an {@link IOException}
    */
   public void setup() throws IOException {
      findFiles();
      checkHooks();
   }

   /**
    * Locates the jacocoagent.jar and bluej.properties
    */
   private void findFiles() {
      String userDir = module.getPreferenceStore().getLocation();
      agentFile = new File(userDir, AGENT_FILE_NAME);
      propertyFile = new File(userDir, PROPERTY_FILE_NAME);
   }

   /**
    * Validates that the bluej properties file has the required VM arguments and
    * that the agent jarfile is present.
    * <p>
    * If jarfile is missing, then it is extracted from the plugin's jar file,
    * and placed in the required location.</br> If the value under
    * {@link #VM_ARG_KEY} in the properties file is invalid, then a shutdown
    * hook is added to correct the issue. </br> If any of the errors above
    * occur, the user must restart BlueJ before being able to use the
    * plugin.</br>
    * 
    * @throws IOException
    *            If any error is encountered while reading the properties,
    *            throws an {@link IOException}
    */
   private void checkHooks() throws IOException {

      Properties props = new Properties();

      props.load(new FileInputStream(propertyFile));

      Object current = props.get(VM_ARG_KEY);
      vmArgsToAdd = buildVMArgs();
      // copy the jar to where we expect it if it doesnt already exist
      if (!agentFile.exists()) {
         copyAgent(getClass().getProtectionDomain().getCodeSource()
                  .getLocation().getFile());
      }
      /*
       *  if the VM args are missing or don't contain our javaagent,
       *  add a shutdown hook and stop our extension.
       */
      if (current == null || !current.toString().contains(buildAgentPath())) {
         JOptionPane
                  .showMessageDialog(
                           module.getBlueJFrame(),
                           "This looks like a setting changed or it is your first time running"
                                    + " Code Coverage.\nPlease restart bluej for it to take effect.",
                           "Code Coverage", JOptionPane.PLAIN_MESSAGE);
         addShutdownHook();
         throw new ExtensionUnloadedException();
      } else {
         updateVmArguments();
      }
   }

   /**
    * Generates the VM Argument for locating the agent jarfile
    * 
    * @return javaagent VM flag with the path to the javaagent
    */
   private String buildAgentPath() {
      return "-javaagent:" + agentFile.getAbsolutePath();
   }

   /**
    * Builds the entire VM Argument string that will be added to the
    * bluej.properties file.
    * <p>
    * Uses {@link #buildAgentPath()} for locating the jar file, {@link #port}
    * for the port number, and {@link #getExcludes()} for any classes to ignore
    * when collecting coverage information.
    * 
    * @return Complete VM Argument for the extension to properly function.
    */
   private String buildVMArgs() {
      String arg = buildAgentPath() + "=output=tcpclient,port=" + port
               + getExcludes();
      return arg;
   }

   /**
    * Generates classes to ignore based off preferences.
    * 
    * @return Single comma seperated String for classes to ignore.
    */
   private String getExcludes() {
      StringBuilder buildExcludes = new StringBuilder();
      // turn list of excluded packages into a single colon seperated string.
      for (String ex : module.getPreferenceManager().getExcludesPrefs().getExcludedPrefs()) {
         buildExcludes.append(":" + ex);
      }
      String rtn = buildExcludes.toString();
      if (!rtn.isEmpty()) {
         rtn = ",excludes=" + rtn.substring(1);
      }
      return rtn;
   }

   /**
    * Registers a shutdown hook if one does not already exist.
    * <p>
    * This adds our vm arguments generated by {@link #buildVMArgs()} to
    * bluej.properties, and prevents BlueJ from overwriting our changes to the
    * properties file.
    */
   public void addShutdownHook() {
      if (!hooked) {
         hooked = true;
         final Properties props = new Properties();
         Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
               try {

                  props.load(new FileInputStream(propertyFile));
                  Object current = props.get(VM_ARG_KEY);
                  // make sure the vm arguments still don't contain our javaagent flag
                  if (current == null
                           || !current.toString().contains(vmArgsToAdd)) {
                     props.put(VM_ARG_KEY,
                              replaceVmArgs(current.toString(), buildVMArgs()));
                     props.store(new FileOutputStream(propertyFile), "");

                  }
               } catch (Exception e) {
                  e.printStackTrace();

               }
            }

         }));
      }

   }

   /**
    * Fixes any errors in the VM arguments loaded from the properties file.
    * <p>
    * This takes care of the issue where other VM Arguments are present that we
    * want to preserve.
    * 
    * @param currentArgs
    *           All existing VM Args in the properties file.
    * @param newArgs
    *           the VM Args we want to add to the properties file.
    * @return The VMArg to add
    */
   private static String replaceVmArgs(String currentArgs, String newArgs) {
      int javaagent = currentArgs.indexOf("-javaagent");
      StringBuilder builder = new StringBuilder(currentArgs);
      // if our vm arg is present
      if (javaagent >= 0) {
         // where our arg ends
         int last = currentArgs.indexOf(" ", javaagent);
         builder.delete(javaagent, last != -1 ? last : currentArgs.length());
         builder.insert(javaagent, newArgs + " ");

      } else {
         // if it is not present add it
         builder.append(" " + newArgs);
      }
      return builder.toString();
   }

   /**
    * Updates any out to date information in our VM Argument that we saved.
    * <p>
    * This increases the port number when it is executed, and updates the
    * properties file, so if anyother instance of BlueJ is launched while this
    * instance is open, it will attempt to use a different port to collect
    * coverage information.
    */
   private void updateVmArguments() {
      final Properties props = new Properties();

      FileInputStream fis = null;
      FileOutputStream fos = null;
      try {
         fis = new FileInputStream(propertyFile);

         props.load(fis);
         Object current = props.get(VM_ARG_KEY);
         // if our argument is in the properties file
         if (current != null) {
            String currentVM = current.toString();
            // pull out the saved port
            Pattern portRegex = Pattern.compile("port=([0-9]+)");
            Matcher match = portRegex.matcher(currentVM);
            match.find();
            Integer newPort = Integer.parseInt(match.group(1));
            this.port = newPort;
            // if the saved port is outside of the range of START_PORT and
            // END_PORT, reset it to START_PORT
            if (newPort != null && (newPort > END_PORT || newPort < START_PORT)) {
               newPort = START_PORT;
            }

            // increase the port number and update the properties file
            newPort += 1;
            String newArgs = match.replaceAll("port=" + newPort);
            props.put(VM_ARG_KEY, newArgs);
            fos = new FileOutputStream(propertyFile);
            props.store(fos, "Updated port for Coverage");
            System.out.println("Updating Coverage port for next app to "
                     + newPort);
            module.getPreferenceStore().setPreference(PORT_NUMBER, "" + newPort);

         }
      } catch (Exception e) {
         e.printStackTrace();

      } finally {
         close(fis);
         close(fos);
      }
   }

   /**
    * Extracts the jacocoagent.jar from the Extension's jar file and into the
    * local file system.
    * 
    * @param jarFile
    *           the location of the Extension's jar file.
    * @throws IOException
    *            If any error is encountered while reading or writing the jar
    *            file, throws an {@link IOException}
    */
   private void copyAgent(String jarFile) throws IOException {
      JarFile jar = new JarFile(jarFile);
      Enumeration<JarEntry> jarEnum = jar.entries();
      while (jarEnum.hasMoreElements()) {
         JarEntry file = jarEnum.nextElement();
         if (file.getName().equals("jacocoagent.jar")) {
            InputStream is = jar.getInputStream(file);
            FileOutputStream fos = new FileOutputStream(agentFile);
            try {
               while (is.available() > 0) {
                  fos.write(is.read());
               }
            } finally {
               close(is);
               close(fos);
            }
         }
      }
   }

   /**
    * Utility method for swallowing IOExceptions when closing a stream.
    * 
    * @param stream
    *           stream to close.
    */
   public static void close(Closeable stream) {
      try {
         if (stream != null) {
            stream.close();
         }
      } catch (IOException ex) {

      }
   }

   /**
    * Utility method for find the root of an exception.
    * 
    * @param e
    *           exception to trace.
    * @return the Actual exception at the root.
    */
   private static Throwable getRootCause(Throwable e) {
      Throwable root = e;
      while (root.getCause() != null) {
         root = root.getCause();
      }
      return root;
   }
}
