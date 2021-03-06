package bluej.codecoverage.ui.button;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import bluej.codecoverage.main.CodeCoverageModule;
import bluej.extensions.BPackage;
import bluej.extensions.BlueJ;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;

/**
 * Generates an item on a Class's popup menu when that class is right clicked.
 * 
 * @author Ian
 * 
 */
@RequiredArgsConstructor
public class CoverageMenuBuilder {
   private final CodeCoverageModule module;
   private final BPackage pack;
   private JToggleButton coverageButton;
   /**
    * Adds the Start Coverage/End Coverage button to BlueJ's frame.
    * @throws PackageNotFoundException
    * @throws ProjectNotOpenException
    */
  public void build()  throws PackageNotFoundException, ProjectNotOpenException {
     JFrame frame = (JFrame) pack.getFrame();
     Container content = frame.getContentPane();
     BorderLayout layout = (BorderLayout) content.getLayout();
     JSplitPane comp = (JSplitPane) layout
           .getLayoutComponent(BorderLayout.CENTER);
     JComponent leftBar = (JComponent) ((BorderLayout) ((JComponent) comp
           .getLeftComponent()).getLayout())
           .getLayoutComponent(BorderLayout.WEST);

     JPanel coverage = new JPanel();

     coverageButton = CoverageButtonFactory.get(module).createButton();

     coverage.setLayout(new BoxLayout(coverage, BoxLayout.Y_AXIS));
     coverage.setAlignmentX(0.5f);
     coverage.add(coverageButton);
     coverageButton.setFont(coverageButton.getFont().deriveFont(0, 10f));
     leftBar.add(coverage, 1);
  }

   public void remove() {
      coverageButton.getParent().remove(coverageButton);
   }

  
}
