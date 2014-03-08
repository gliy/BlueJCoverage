/*
 * 
 */
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
import java.net.ConnectException;
import java.net.ServerSocket;
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

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
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
   private static final String VM_ARG_KEY = "bluej.vm.args";
   private static final String PORT_NUMBER = "port.number";

   private static final String HELP_URL = "https://github.com/gliy/BlueJCoverage/wiki/Help";
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
    * 
    * @throws IOException
    */
   private CoverageUtilities(BlueJ bluej) throws IOException {
      this.bluej = bluej;
      prefs = CoveragePrefManager.getPrefs(bluej).get();
      port = Integer.parseInt(bluej.getExtensionPropertyString(PORT_NUMBER, "" + START_PORT));
      port = Math.max(START_PORT, port);
      setup();
      
      setupListener();

   }

   private void setupListener() {
      try {
         coverageListener = new BreakoutClassloader(bluej.getUserConfigDir())
               .loadClass(CoverageListener.class.getName())
               .getConstructor(Integer.TYPE).newInstance(port);
      } catch (Exception e) {
         addShutdownHook();
         e.printStackTrace();
      }
   }

   public static CoverageUtilities create(BlueJ blueJ) throws IOException {
      if (utils != null) {
         throw new RuntimeException("Utils class already created");
      }
      utils = new CoverageUtilities(blueJ);
      return utils;
   }

   public static CoverageUtilities get() {
      if (utils == null) {
         throw new RuntimeException("Utils class not created");
      }
      return utils;
   }

   public void clearResults() {
      try {
         Method clearMethod = coverageListener.getClass().getDeclaredMethod(
               "clearResults");
         clearMethod.invoke(coverageListener);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public List<CoveragePackage> getResults(File file) {
      ObjectInputStream input = null;
      List<CoveragePackage> rtn = new ArrayList<CoveragePackage>();
      try {
         Method resultsMethod = coverageListener.getClass().getDeclaredMethod(
               "getResults", File.class);
         input = (ObjectInputStream) resultsMethod.invoke(coverageListener,
               file);
         Object readIn;
         while ((readIn = input.readObject()) != null) {
            rtn.add((CoveragePackage) readIn);
         }
      } catch (Exception e) {
         JOptionPane.showMessageDialog(bluej.getCurrentFrame(),
               buildErrorMessage(getRootCause(e).getMessage()),
               "Code Coverage Error", JOptionPane.ERROR_MESSAGE);
         return null;
      } finally {
         close(input);
      }
      return rtn;
   }

   private JComponent buildErrorMessage(String ex) {
      // "An error was encountered while gathering coverage data\n"
      // getRootCause(e).getMessage()
      JTextPane error = new JTextPane();
      error.setContentType("text/html");
      error.setEditable(false);
      error.setText(String
            .format(
                  "An error was encountered while gathering coverage data <br> "
                        + "<span style=\"color:red;font-size:14px;font-family:monospace\">%s</span><br>"
                        + "<a href=%s>Visit the FAQ for more information</a>",
                  ex, HELP_URL));
      error.setBackground(new Color(240, 240, 240));
      error.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent e) {
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
    * Sets up the Utilites class so it can properly manage associations.
    * 
    * @param bluejApp
    *           the running blueJ application
    * @throws IOException
    */
   public void setup() throws IOException {
      findFiles();
      checkHooks();
   }

   private void findFiles() {
      agentFile = new File(bluej.getUserConfigDir(), AGENT_FILE_NAME);
      propertyFile = new File(bluej.getUserConfigDir(), PROPERTY_FILE_NAME);
   }

   /**
    * Validates that the bluej properties file has the required VM arguments,
    * and that the agent jarfile is present.
    * 
    * @throws IOException
    */
   private void checkHooks() throws IOException {

      Properties props = new Properties();

      props.load(new FileInputStream(propertyFile));

      Object current = props.get(VM_ARG_KEY);
      vmArgsToAdd = buildVMArgs();
      if (!agentFile.exists()) {
         copyAgent(getClass().getProtectionDomain().getCodeSource()
               .getLocation().getFile());
      }
      if (current == null || !current.toString().contains(buildAgentPath())) {
         JOptionPane
               .showMessageDialog(
                     bluej.getCurrentFrame(),
                     "This looks like a setting changed or it is your first time running"
                           + " Code Coverage.\nPlease restart bluej for it to take effect.",
                     "Code Coverage", JOptionPane.PLAIN_MESSAGE);
         addShutdownHook();
         throw new ExtensionUnloadedException();
      } else {
         updateVmArguments();
      }
   }

   private String buildAgentPath() {
      return "-javaagent:" + agentFile.getAbsolutePath();
   }

   private String buildVMArgs() {
      String arg = buildAgentPath() + "=output=tcpclient,port=" + port
            + getExcludes();
      return arg;
   }

   private String getExcludes() {
      StringBuilder buildExcludes = new StringBuilder();
      for (String ex : prefs.getExcluded()) {
         buildExcludes.append(":" + ex);
      }
      String rtn = buildExcludes.toString();
      if (!rtn.isEmpty()) {
         rtn = ",excludes=" + rtn.substring(1);
      }
      return rtn;
   }


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

                  if (current == null
                        || !current.toString().contains(vmArgsToAdd)) {
                     props.put(VM_ARG_KEY, replaceVmArgs(current.toString(), buildVMArgs()));
                     props.store(new FileOutputStream(propertyFile), "");

                  }
               } catch (Exception e) {
                  e.printStackTrace();

               }
            }

           
         }));
      }

   }
   
   private static String replaceVmArgs(String currentArgs, String newArgs) {
      int javaagent = currentArgs.indexOf("-javaagent");
      StringBuilder builder = new StringBuilder(currentArgs);
      if(javaagent >= 0){
         builder.delete(javaagent, currentArgs.indexOf(" ", javaagent));
         builder.insert(javaagent, newArgs + " ");
        
      } else {
         builder.append(" " + newArgs);
      }
      return builder.toString();
   }

   private void updateVmArguments() {
      final Properties props = new Properties();

      FileInputStream fis = null;
      FileOutputStream fos = null;
      try {
         fis = new FileInputStream(propertyFile);

         props.load(fis);
         Object current = props.get(VM_ARG_KEY);
         if (current != null) {
            String currentVM = current.toString();
            Pattern portRegex = Pattern.compile("port=([0-9]+)");
            Matcher match = portRegex.matcher(currentVM);
            match.find();
            Integer newPort = Integer.parseInt(match.group(1));
            this.port = newPort;
            if (newPort != null && (newPort > END_PORT || newPort < START_PORT)) {
               newPort = START_PORT;
            }

            newPort += 1;
            String newArgs = match.replaceAll("port=" + newPort);
            props.put(VM_ARG_KEY, newArgs);
            fos = new FileOutputStream(propertyFile);
            props.store(fos, "Updated port for Coverage");
            System.out.println("Updating Coverage port for next app to "
                  + newPort);
            bluej.setExtensionPropertyString(PORT_NUMBER, "" + newPort);

         }
      } catch (Exception e) {
         e.printStackTrace();

      } finally {
         close(fis);
         close(fos);
      }
   }

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

   public static void close(Closeable stream) {
      try {
         if (stream != null) {
            stream.close();
         }
      } catch (IOException ex) {

      }
   }

   private static Throwable getRootCause(Throwable e) {
      Throwable root = e;
      while (root.getCause() != null) {
         root = root.getCause();
      }
      return root;
   }
}
