package bluej.codecoverage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;

import base.CoverageTestBase;
import base.MockCodeCoverageModule;
import bluej.codecoverage.main.CodeCoverageModule;

public class CoverageActionTest extends CoverageTestBase {
   private CodeCoverageModule module;
   private CoverageAction action;
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      module = new MockCodeCoverageModule();
      action = new CoverageAction(module);
   }
   public void testStartCoverage() throws Exception {
      action.itemStateChanged(new ItemEvent(mock(ItemSelectable.class), 0, 0, ItemEvent.SELECTED));
      verify(module.getCoverageUtilities(),only()).clearResults();
      
   }
}
