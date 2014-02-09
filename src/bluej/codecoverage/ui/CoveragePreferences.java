package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bluej.extensions.BlueJ;
import bluej.extensions.PreferenceGenerator;

public class CoveragePreferences implements PreferenceGenerator
{
    private DefaultListModel<String> ignore;
    private BlueJ bluej;
    private List<String> ignoredPackages;
    public CoveragePreferences(BlueJ bluej) {
        this.bluej = bluej;
        ignoredPackages = new ArrayList<String>();
        ignore = new DefaultListModel<String>();
        
    }
    @Override
    public JPanel getPanel()
    {
        JButton addIgnore = new JButton("Add package to ignore");
        ignore.addElement("test");
        JPanel rtn = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.add(addIgnore);
        rtn.add(buttons, BorderLayout.EAST);
        JList<String> ignoreList = new JList<String>(ignore);

        JScrollPane ignoreScrollPane = new JScrollPane(ignoreList );
        ignoreScrollPane.setBorder(BorderFactory.createTitledBorder("Ignored"));
        rtn.add(ignoreScrollPane , BorderLayout.CENTER);
        return rtn;
    }

    @Override
    public void loadValues()
    {
       // bluej.getExtensionPropertyString("ignore", "");
    }

    @Override
    public void saveValues()
    {
        // TODO Auto-generated method stub

    }
    

}
