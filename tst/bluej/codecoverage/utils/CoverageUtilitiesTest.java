package bluej.codecoverage.utils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;

import org.mockito.Mockito;

import junit.framework.TestCase;
import base.MockCodeCoverageModule;

public class CoverageUtilitiesTest extends TestCase {
   private CoverageUtilities utils;
   private MockCodeCoverageModule module;
   private static final int TEST_PORT = 9000;
   private static final String DEFAULT_ARGS = "-javaagent:test";
   @Override
   protected void setUp() throws Exception {
      // TODO Auto-generated method stub
      super.setUp();
      this.module = new MockCodeCoverageModule();
      when(
               module.getPreferenceStore().getPreference(eq("port.number"),
                        anyString())).thenReturn("" + TEST_PORT);
      when(module.getPreferenceStore().getPreference(eq("coverage.excludes")))
               .thenReturn("");
      utils = new CoverageUtilities(module);
   }

   public void testRootCause() throws Exception {

      assertEquals(
               SocketTimeoutException.class,
               CoverageUtilities.getRootCause(
                        new IOException(new RuntimeException(
                                 new SocketTimeoutException()))).getClass());
      assertEquals(IOException.class,
               CoverageUtilities.getRootCause(new IOException()).getClass());
   }

   public void testSetupListener() throws Exception {
      // new CoverageUtilities(TEST_PORT).setupListener();
   }

   public void testReplaceVMArgs() throws Exception {
      String args = "-ea";
      String newArgs = "-javaagent:/path,port=9999";
      String actual = CoverageUtilities.replaceVmArgs(args, newArgs);
      assertEquals(args + " " + newArgs, actual);

      newArgs = "-javaagent:/diff/path";
      assertEquals(args + " " + newArgs,
               CoverageUtilities.replaceVmArgs(actual, newArgs).trim());

   }

   public void testUpdateVMArgs() throws Exception {

      File testFile = File.createTempFile("tmp", ".properties");

      long size = testFile.getTotalSpace();
      utils.propertyFile = testFile;
      utils.updateVmArguments();
      assertEquals(size, testFile.getTotalSpace());

   }

   public void testClose() throws Exception {
      InputStream stream = mock(InputStream.class);
      CoverageUtilities.close(stream);
      verify(stream, only()).close();
   }

   public void testBuildVMArgs() throws Exception {
      utils.agentFile = File.createTempFile("tmp", "tmp");
      String expected = "-javaagent:" + utils.agentFile.getAbsolutePath()
               + "=output=tcpclient,port=" + TEST_PORT;
      assertEquals(expected, utils.buildVMArgs());
   }

   public void testGetExcludes() throws Exception {
      assertEquals("", utils.getExcludes());
      when(module.getPreferenceStore().getPreference(eq("coverage.excludes")))
               .thenReturn("someclass,otherclass");
      assertEquals(",excludes=someclass:otherclass", utils.getExcludes());

   }

   private static void addPort(File to, Integer port) throws FileNotFoundException {
      PrintWriter wr = new PrintWriter(to);
      wr.println("bluej.vm.args=" + DEFAULT_ARGS + ",port=" + port);
      wr.close();
   }
}
