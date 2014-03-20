package bluej.codecoverage.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.jacoco.report.html.HTMLFormatter;

import bluej.codecoverage.utils.serial.CoverageBridge;
import bluej.codecoverage.utils.serial.CoveragePackage;

/**
 * Backend for collecting coverage information from the java agent. This class
 * triggers a dump of all collected coverage information when requested,
 * serializes it, then returns an objectinputstream to the caller to deserialize
 * the data.
 * 
 * @see CoverageBridge
 * 
 * @author ikingsbu
 * 
 */
public class CoverageListener {
   /** Address of the port to listen on */
   private static final String ADDRESS = "localhost";
   /** Current port to listen on */
   private final int port;
   /** Coverage data collector */
   private Handler current;

   /**
    * Start the server to listening for connections on a seperate thread.
    * 
    * @param port
    *           port number to listen on
    */
   public CoverageListener(int port) throws IOException {
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
               current = new Handler(server.accept());
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
    *           base directory to search for classess who should be included in
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

   /**
    * Manages all interactions with the jacoco agent by sending requests and
    * recieveing responses from a local port.
    * <p>
    * After recieving information from the agent, it marshalls the data into a
    * Serializable form, so it can be sent to different class loaders.
    * 
    * </br> 
    * Creation of the Serializable forms are handled through class to
    * {@link CoverageBridge}.
    * 
    * @author ikingsbu
    * 
    */
   private static class Handler implements Runnable {

      private final Socket socket;

      private final RemoteControlReader reader;

      private ExecutionDataStore executionData = new ExecutionDataStore();
      private SessionInfoStore sesionInfo = new SessionInfoStore();
      private final RemoteControlWriter trigger;
      private Object lock;

      Handler(final Socket socket) throws IOException {
         this.socket = socket;
         this.lock = new Object();
         // Just send a valid header:
         trigger = new RemoteControlWriter(socket.getOutputStream());

         reader = new RemoteControlReader(socket.getInputStream());
         reader.setSessionInfoVisitor(sesionInfo);
         reader.setExecutionDataVisitor(executionData);
      }

      @Override
      public void run() {
         try {

            while (reader.read()) {
               synchronized (lock) {
                  lock.notifyAll();
               }
            }
            socket.close();
         } catch (final Exception e) {
            e.printStackTrace();
         }
      }

      public void clearResults() {
         if (socket.isConnected()) {
            System.out.println("Clear requested");
            try {
               // resets the coverage information to prepare for a new
               // collection
               trigger.visitDumpCommand(false, true);
               executionData.reset();
               sesionInfo = new SessionInfoStore();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

      public ObjectInputStream getResults(final File file) {
         try {
            if (socket.isConnected()) {
               System.out.println("Dump requested");
               synchronized (lock) {
                  // dumps the information and resets all collected coverage
                  // information
                  trigger.visitDumpCommand(true, false);
                  trigger.sendCmdOk();
                  trigger.flush();
                  lock.wait();
               }

               // creates an input/output pipe to send the coverage information
               PipedInputStream inPipe = new PipedInputStream();
               final PipedOutputStream outPipe = new PipedOutputStream(inPipe);
               final CoverageBuilder coverageBuilder = new CoverageBuilder();
               final Analyzer analyzer = new Analyzer(executionData,
                        coverageBuilder);

               analyzer.analyzeAll(file);
               new Thread(new Runnable()

               {

                  @Override
                  public void run() {
                     try {
                        ObjectOutputStream outputStream = new ObjectOutputStream(
                                 outPipe);

                        IBundleCoverage bundle = coverageBuilder
                                 .getBundle("Run");

                        // send all gathered coverage information
                        for (IPackageCoverage coverage : bundle.getPackages()) {
                           outputStream.writeObject(CoverageBridge
                                    .toSerializable(coverage));
                        }
                        outputStream.writeObject(null);
                     } catch (Exception e) {
                        e.printStackTrace();
                     } finally {
                        close(outPipe);

                     }
                  }

               }).start();
               return new ObjectInputStream(inPipe);
            } else {
               throw new IllegalStateException("Socket is not connected");
            }

         } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);
         }
      }

      private static void close(Closeable close) {
         try {
            close.close();
         } catch (Exception e) {

         }
      }
   }

}
