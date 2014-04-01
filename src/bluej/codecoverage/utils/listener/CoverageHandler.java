package bluej.codecoverage.utils.listener;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

import bluej.codecoverage.utils.serial.CoverageBridge;

/**
 * Manages all interactions with the jacoco agent by sending requests and
 * receiving responses from a local port.
 * <p>
 * After receiving information from the agent, it marshals the data into a
 * Serializable form, so it can be sent to different class loaders.
 * 
 * </br> Creation of the Serializable forms are handled through class to
 * {@link CoverageBridge}.
 * 
 * 
 */
class CoverageHandler implements Runnable {

   private final Socket socket;
   /** Reads from the coverage agent */
   private final RemoteControlReader reader;
   /** Stores information gathered from the coverage agent */
   private ExecutionDataStore executionData = new ExecutionDataStore();
   /** Stores information gathered from the coverage agent */
   private SessionInfoStore sesionInfo = new SessionInfoStore();
   /** Writes to jacoco agent */
   private final RemoteControlWriter trigger;
   /** lockable object to make sure we wait until all input is read */
   private Object lock;

   CoverageHandler(final Socket socket) throws IOException {
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

         // keep the connection alive
         // until client disconnects
         while (reader.read()) {
            /*
             * makes sure when we issue a dump command that we wait for the
             * client to actually finish sending coverage information before
             * starting to process it.
             */
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
               // wait until the agent is done sending data
               lock.wait();
            }

            // creates an input/output pipe to send the coverage information
            PipedInputStream inPipe = new PipedInputStream();
            final PipedOutputStream outPipe = new PipedOutputStream(inPipe);
            final CoverageBuilder coverageBuilder = new CoverageBuilder();
            final Analyzer analyzer = new Analyzer(executionData,
                  coverageBuilder);
            // read and process all coverage information that the client has sent so far
            analyzer.analyzeAll(file);
            new Thread(new Runnable() {

               @Override
               public void run() {
                  try {
                     ObjectOutputStream outputStream = new ObjectOutputStream(
                           outPipe);
                     // put the information into a bundle
                     IBundleCoverage bundle = coverageBuilder
                           .getBundle("Run");

                     // send all gathered coverage information
                     for (IPackageCoverage coverage : bundle.getPackages()) {
                        // translate jacoco coverage into our serializable version
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