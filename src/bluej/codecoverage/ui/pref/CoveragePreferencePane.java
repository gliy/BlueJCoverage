package bluej.codecoverage.ui.pref;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import bluej.codecoverage.main.CodeCoverageModule;
import bluej.codecoverage.pref.PreferenceManager;
import bluej.codecoverage.pref.option.BasePreferenceOption.Pref;
import bluej.codecoverage.pref.option.ColorPreferences;
import bluej.codecoverage.pref.option.ExcludesPreferences;
import bluej.codecoverage.utils.CoverageUtilities;
import bluej.extensions.PreferenceGenerator;

/**
 * Represents the GUI panel that BlueJ displays when a user opens the
 * preferences for the extension.
 * 
 * @author Ian
 * 
 */
public class CoveragePreferencePane implements PreferenceGenerator {

   private ExcludesPreferences excludedPrefs;
   private ColorPreferences colorPrefs;
   private DefaultListModel<String> ignore;
   private PreferenceManager prefManager;
   private CoverageUtilities utils;

   public CoveragePreferencePane(CodeCoverageModule module) {
      this.excludedPrefs = module.getPreferenceManager().getExcludesPrefs();
      this.colorPrefs = module.getPreferenceManager().getColorPrefs();
      this.utils = module.getCoverageUtilities();
      this.prefManager = module.getPreferenceManager();
   }

   @Override
   public JPanel getPanel() {
      loadValues();

      final JPanel rtn = new JPanel(new BorderLayout());
      JPanel buttons = new JPanel();
      buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

      JButton guiButton = new JButton("GUI Settings");
      buttons.add(guiButton);

      guiButton.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showConfirmDialog(rtn, getGUIOptions(), "GUI Settings",
                  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
         }
      });
      rtn.add(buttons, BorderLayout.SOUTH);
      rtn.add(getExcluded(), BorderLayout.CENTER);

      return rtn;
   }

   @Override
   public void loadValues() {

   }

   @Override
   public void saveValues() {
      // PreferenceManager.getPrefs().save();

   }

   private JPanel getExcluded() {

      ignore = new DefaultListModel<String>() {
         {
            for (String excluded : excludedPrefs.getExcludedPrefs()) {
               super.addElement(excluded);
            }
         }

         @Override
         public void addElement(String element) {
            super.addElement(element);
            utils.addShutdownHook();
         }

         @Override
         public String remove(int index) {
            utils.addShutdownHook();
            return super.remove(index);
         }

      };

      JPanel rtn = new JPanel(new BorderLayout());
      final JList<String> ignoreList = new JList<String>(ignore);
      ignoreList.setVisibleRowCount(10);
      JScrollPane ignoreScrollPane = new JScrollPane(ignoreList);
      ignoreScrollPane.setBorder(BorderFactory
            .createTitledBorder("Excluded (Changes require BlueJ restart)"));

      JButton addIgnore = new JButton("Add package");

      addIgnore.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            String ignoreInput = JOptionPane
                  .showInputDialog("Enter package/class to excluded");
            if (ignoreInput != null) {
               ignore.addElement(ignoreInput);
            }
         }
      });

      JButton removeIgnore = new JButton("Remove package");

      removeIgnore.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {
            int selected = ignoreList.getSelectedIndex();
            if (selected != -1) {
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

   private JPanel getGUIOptions() {
      final JPanel main = new JPanel();
      main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

      for (final Pref<Color> colorPref : colorPrefs.getAllOptions()) {

         JPanel opt = new JPanel(new FlowLayout(FlowLayout.LEFT));

         final JButton color = new JButton();

         color.setContentAreaFilled(false);
         color.setBackground(colorPrefs.getValue(colorPref));
         color.setForeground(color.getBackground());
         color.setOpaque(true);
         color.setBorderPainted(true);
         color.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

               Color choice = JColorChooser.showDialog(main,
                     "Choose color for " + colorPref.key, color.getBackground());
               if (choice != null) {
                  colorPrefs.save(colorPref, choice);
                  color.setBackground(choice);
               }

            }
         });
         color.setPreferredSize(new Dimension(40, 20));
         opt.add(color);
         opt.add(new JLabel(colorPref.key));
         main.add(opt);

      }
      return main;
   }

}
