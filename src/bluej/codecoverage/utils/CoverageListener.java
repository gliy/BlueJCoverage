package bluej.codecoverage.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.jacoco.core.runtime.RuntimeData;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.html.HTMLFormatter;

import bluej.codecoverage.utils.serial.CoverageBridge;

/**
 * Backend for collecting coverage information from the java agent.
 * This class triggers a dump of all collected coverage information when requested,
 * serializes it, then returns an objectinputstream to the caller to deserialize the data.
 * 
 * @see CoverageBridge
 * 
 * @author ikingsbu
 *
 */
public class CoverageListener
{

    private static final String ADDRESS = "localhost";

    private final int port;
    private Handler current;
    /**
     * Start the server as a standalone program.
     * 
     * @param args
     * @throws IOException
     */
    public CoverageListener(int port) throws IOException {
        this.port= port;
        if(!listenerThread.isAlive()) {
            listenerThread.start();
        }
        
    }
    private Thread listenerThread = new Thread(new Runnable()
    {
        
        @Override
        public void run()
        {
            try {
                System.out.println("connecting...");
                final ServerSocket server = new ServerSocket(port, 0,
                        InetAddress.getByName(ADDRESS));
                
                while (true) {
                    current = new Handler(server.accept());
                    System.out.println("connected");
                    new Thread(current).start();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    });
    public ObjectInputStream getResults(File file) {
        return current.getResults(file);
    }
    public void clearResults() {
        current.clearResults();
    }

    private static class Handler implements Runnable {

        private final Socket socket;

        private final RemoteControlReader reader;

        private  ExecutionDataStore executionData = new ExecutionDataStore(); 
        private SessionInfoStore sesionInfo = new SessionInfoStore();
        private final RemoteControlWriter trigger;
        private RuntimeData data;
        Handler(final Socket socket) throws IOException {
            this.socket = socket;
            this.data = new RuntimeData();
            // Just send a valid header:
            trigger = new RemoteControlWriter(socket.getOutputStream());
            
            reader = new RemoteControlReader(socket.getInputStream());
            reader.setSessionInfoVisitor(sesionInfo);
            reader.setExecutionDataVisitor(executionData);
            
            
        }

        public void run() {
            try {
     
                while (reader.read()) {
                 
                }             
                socket.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        public void clearResults() {
            if(socket.isConnected()) {
                System.out.println("Clear requested");
                try
                {
                    // resets the coverage information to prepare for a new collection
                    trigger.visitDumpCommand(false, true);
                    executionData.reset();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        public ObjectInputStream getResults(final File file) {
            try
            {
                if(socket.isConnected()) {
                    System.out.println("Dump requested");
  
                    // dumps the information and resets all collected coverage information
                    trigger.visitDumpCommand(true, false);
                    trigger.sendCmdOk();
                    
                    data.collect(executionData, sesionInfo, true);
                    
                    // creates an input/output pipe to send the coverage information
                    PipedInputStream inPipe = new PipedInputStream();
                    final PipedOutputStream outPipe = new PipedOutputStream(inPipe);
                    final CoverageBuilder coverageBuilder = new CoverageBuilder();
                    final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
                   // analyzer.analyzeAll(file);
                  
                    analyzer.analyzeAll(file.getAbsolutePath(), null);
                  //  IBundleCoverage bundle = coverageBuilder.getBundle("Run");
                    //test(bundle, file);

                    new Thread(new Runnable()
                    
                    {
                        
                        @Override
                        public void run()
                        {
                            try {
                                ObjectOutputStream outputStream = new ObjectOutputStream(outPipe);
                                
                                IBundleCoverage bundle = coverageBuilder.getBundle("Run");
   
                                // send all gathered coverage information
                                for (IPackageCoverage coverage : bundle.getPackages())
                                {
                                    outputStream.writeObject(CoverageBridge.toSerializable(coverage));
                                }
                                outputStream.writeObject(null);
                            }catch(Exception e) {
                                e.printStackTrace();
                            } finally {
                                close(outPipe);
                                
                            }
                        }

                       
                    }).start();
                    return new ObjectInputStream(inPipe);
                }
               
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
            }
            return null;
        } 
        private void test(IBundleCoverage bundle, File src) throws Exception
        {
            DirectorySourceFileLocator loc = new DirectorySourceFileLocator(src, "UTF-8", 4);
            File test = new File("F:\\test", "test");
            System.out.println(test.getAbsolutePath());
          
            IReportVisitor format = new HTMLFormatter().createVisitor(
                new FileMultiReportOutput(test));
            format.visitInfo(sesionInfo.getInfos(), executionData.getContents());
            format.visitBundle(bundle, loc);
         }
        
        private static void close(Closeable close) {
            try {
                close.close();
            }catch(Exception e) {
                
            }
        }
    }

}
