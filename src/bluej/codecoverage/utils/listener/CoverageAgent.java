package bluej.codecoverage.utils.listener;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.jacoco.report.html.HTMLFormatter;

import bluej.codecoverage.utils.serial.CoverageBridge;
import bluej.codecoverage.utils.serial.CoveragePackage;

/**
 * Backend for collecting coverage information from the java agent. This class
 * triggers a dump of all collected coverage information when requested,
 * serializes it, then returns an ObjectInputStream to the caller to deserialize
 * the data.
 * 
 * @see CoverageBridge
 * 
 * @author ikingsbu
 * 
 */
public class CoverageAgent {
   /** Address of the port to listen on */
   private static final String ADDRESS = "localhost";
   /** Current port to listen on */
   private final int port;
   /** Coverage data collector */
   private CoverageHandler current;

   /**
    * Start the server to listening for connections on a seperate thread.
    * 
    * @param port
    *           port number to listen on
    */
   public CoverageAgent(int port) throws IOException {
      this.port = port;
      if (!listenerThread.isAlive()) {
         listenerThread.start();
      }

   }

   /**
    * Thread that creates {@link #current} and makes sure the connection stays
    * alive.
    */
   private Thread listenerThread = new Thread(new Runnable() {

      @Override
      public void run() {
         try {
            System.out.println("connecting on port " + port);
            final ServerSocket server = new ServerSocket(port, 0,
                  InetAddress.getByName(ADDRESS));

            while (!server.isClosed()) {
               current = new CoverageHandler(server.accept());
               System.out.println("connected to coverage agent");
               new Thread(current).start();
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   });

   /**
    * Creates an InputStream to Serialize the Coverage information so that it
    * can be used in a different ClassLoder.
    * 
    * 
    * @param file
    *           base directory to search for classes who should be included in
    *           the returned coverage metrics.
    * @return InputStream containing {@link CoveragePackage}s
    */
   public ObjectInputStream getResults(File file) {
      return current.getResults(file);
   }

   /**
    * Resets all coverage information collected so far.
    */
   public void clearResults() {
      current.clearResults();
   }

}
