package bluej.codecoverage.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.BlueJ;
import bluej.extensions.PreferenceGenerator;

public class CoveragePreferences implements PreferenceGenerator
{

    private BlueJ bluej;
    private CurrentPreferences prefs;
    private DefaultListModel ignore;

    public CoveragePreferences(BlueJ bluej)
    {
        this.bluej = bluej;
    }

    @Override
    public JPanel getPanel()
    {
        loadValues();

        final JPanel rtn = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

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
        rtn.add(buttons, BorderLayout.SOUTH);
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

    private JPanel getExcluded()
    {

        ignore = new DefaultListModel()
        {
            {
                for (String excluded : prefs.getExcluded())
                {
                    super.addElement(excluded);
                }
            }

            @Override
            public void addElement(Object element)
            {
                prefs.addExcluded(element.toString());
                super.addElement(element);
                CoverageUtilities.get().addShutdownHook();
            }

            @Override
            public Object remove(int index)
            {

                prefs.removeExcluded(index);
                CoverageUtilities.get().addShutdownHook();
                return super.remove(index);
            }

            
        };

        JPanel rtn = new JPanel(new BorderLayout());
        final JList ignoreList = new JList(ignore);
        ignoreList.setVisibleRowCount(10);
        JScrollPane ignoreScrollPane = new JScrollPane(ignoreList);
        ignoreScrollPane.setBorder(BorderFactory
            .createTitledBorder("Excluded (Changes require BlueJ restart)"));

        JButton addIgnore = new JButton(
            "Add package");

        addIgnore.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                String ignoreInput = JOptionPane
                    .showInputDialog("Enter package/class to excluded");
                if (ignoreInput != null)
                {
                    ignore.addElement(ignoreInput);
                }
            }
        });

        JButton removeIgnore = new JButton(
            "Remove package");

        removeIgnore.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                int selected = ignoreList.getSelectedIndex();
                if (selected != -1)
                {
                    ignore.remove(selected);
                }
            }
        });

        JPanel ignoreButtons = new JPanel();
        ignoreButtons.setLayout(new BoxLayout(ignoreButtons, BoxLayout.Y_AXIS));
        ignoreButtons.add(addIgnore);
        ignoreButtons.add(removeIgnore);

        rtn.add(ignoreButtons, BorderLayout.EAST);
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
                JPanel opt = new JPanel(new FlowLayout(FlowLayout.LEFT));

               
                final JButton color = new JButton();
                color.setBackground((Color) prefs.getPref(key));
                color.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e )
                    {
                      //  if (e.getButton() == MouseEvent.BUTTON1)
                      //  {
                            Color choice = JColorChooser.showDialog(main,
                                "Choose color for " + key.getDisplay(),
                                color.getBackground());
                            if (choice != null)
                            {
                                prefs.setPref(key, choice);
                                color.setBackground(choice);
                            }
                       // }
                    }
                });
                color.setPreferredSize(new Dimension(40, 20));
                opt.add(color);
                opt.add(new JLabel(key.getDisplay()));
              //  opt.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                main.add(opt);
            }
        }
        return main;
    }

}
