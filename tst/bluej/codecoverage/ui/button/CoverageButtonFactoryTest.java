package bluej.codecoverage.ui.button;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import javax.swing.JToggleButton;

import junit.framework.TestCase;
import base.MockCodeCoverageModule;

public class CoverageButtonFactoryTest extends TestCase {

   private MockCodeCoverageModule module;
   @Override
   protected void setUp() throws Exception {
      module = new MockCodeCoverageModule();
   }
   public void testMultipleButtons() throws Exception {
      CoverageButtonFactory factory = CoverageButtonFactory.get(module);
      JToggleButton button1 = factory.createButton();
      assertEquals("Start Coverage", button1.getText().trim());
      JToggleButton button2 = factory.createButton();
      assertEquals("Start Coverage", button2.getText().trim());
      
      
      button1.doClick();
      JToggleButton button3 = factory.createButton();
      assertEquals("End Coverage", button1.getText().trim());
      assertEquals("End Coverage", button2.getText().trim());
      assertEquals("End Coverage", button3.getText().trim());
      verify(module.getCoverageUtilities(), only()).clearResults();
      
      assertEquals(button1.getModel(), button2.getModel());
      
   }
}
