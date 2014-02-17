package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bluej.codecoverage.pref.CoveragePrefManager;
import bluej.codecoverage.pref.CoveragePrefManager.CurrentPreferences;
import bluej.codecoverage.pref.CoveragePrefManager.PrefKey;
import bluej.extensions.BlueJ;
import bluej.extensions.PreferenceGenerator;

public class CoveragePreferences implements PreferenceGenerator
{
  
    private BlueJ bluej;
    private List<String> ignoredPackages;
    private CurrentPreferences prefs;

    public CoveragePreferences(BlueJ bluej)
    {
        this.bluej = bluej;
        ignoredPackages = new ArrayList<String>();

    }

    @Override
    public JPanel getPanel()
    {
        loadValues();
        JButton addIgnore = new JButton(
            "<html>Add package to ignore</br><font color=red> (Requires BlueJ restart)</font></html>");

        final JPanel rtn = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        buttons.add(addIgnore);
        JButton guiButton = new JButton("GUI Settings");
        buttons.add(guiButton);
        guiButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showConfirmDialog(rtn, getGUIOptions(),
                    "GUI Settings", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            }
        });
        rtn.add(buttons, BorderLayout.EAST);

        rtn.add(getExcluded(), BorderLayout.CENTER);
       
        return rtn;
    }

    @Override
    public void loadValues()
    {
        prefs = CoveragePrefManager.getPrefs()
            .load();
    }

    @Override
    public void saveValues()
    {
        CoveragePrefManager.getPrefs()
            .save();

    }
    private JPanel getExcluded() {
        
        DefaultListModel<String> ignore = new DefaultListModel<String>(){
            {
                for(String excluded : prefs.getExcluded()) {
                    super.addElement(excluded);
                }
            }
            @Override
            public void addElement(String element)
            {
                prefs.addExcluded(element);
            }
            @Override
            public boolean removeElement(Object obj)
            {
               
               prefs.removeExcluded(obj.toString());
               return super.removeElement(obj);
            }
        };
      
        JPanel rtn = new JPanel(new BorderLayout());
        JList<String> ignoreList = new JList<String>(ignore);

        JScrollPane ignoreScrollPane = new JScrollPane(ignoreList);
        ignoreScrollPane.setBorder(BorderFactory.createTitledBorder("Ignored"));
        rtn.add(ignoreScrollPane, BorderLayout.CENTER);
        return rtn;
    }
    private JPanel getGUIOptions()
    {
        final JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        for (final PrefKey key : PrefKey.values())
        {
            if (key.getDisplay() != null)
            {
                JPanel opt = new JPanel(new GridLayout(1, 2));
                
                opt.add(new JLabel(key.getDisplay()));
                final JPanel color = new JPanel();
                color.setBackground((Color) prefs.getPref(key));
                color.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mousePressed(MouseEvent e)
                    {
                        if (e.getButton() == MouseEvent.BUTTON1)
                        {
                            Color choice = JColorChooser.showDialog(main,
                                "Choose color for " + key.getDisplay(),
                                color.getBackground());
                            if (choice != null)
                            {
                                prefs.setPref(key, choice);
                                color.setBackground(choice);
                            }
                        }
                    }
                });
                opt.add(color);
                opt.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                main.add(opt);
            }
        }
        return main;
    }

}
