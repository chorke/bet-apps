
package chorke.proprietary.bet.apps.gui.panels;

import chorke.proprietary.bet.apps.StaticConstants;
import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.gui.GuiUtils;
import chorke.proprietary.bet.apps.io.BetIOManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Chorke
 */
public class LoadingPanel extends JPanel{

    private int widthOfPanel = 450;
    
    private DateChooser from;
    private DateChooser by;
    private JList<Tuple<String, String>> selectedLeagues;
    private DefaultListModel<Tuple<String, String>> selectedLeaguesModel;
    private DefaultComboBoxModel<String> countries;
    private DefaultComboBoxModel<String> leagues;
    
    private JPanel countriesAndLeaguesPanel;
    private JPanel datesPanel;
    private JPanel betCompaniesPanel;
    
    private BetIOManager manager;
    private Map<String, Collection<String>> availableLeagues;
    
    private ResourceBundle bundle = StaticConstants.BUNDLE;
    
    public LoadingPanel(BetIOManager manager) throws IllegalArgumentException{
        if(manager == null){
            throw new IllegalArgumentException("Manager cannot be null.");
        }
        this.manager = manager;
        GuiUtils.showWaitingDialog("Inicializing loading");
        init();
        GuiUtils.hideWaitingDialog();
    }
    
    private void init(){
        initCountriesAndLeaguesPanel();
        initDatesPanel();
        initBetCompaniesPanel();
        
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
                .addComponent(datesPanel)
                .addComponent(betCompaniesPanel)
                .addComponent(countriesAndLeaguesPanel));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(datesPanel)
                .addComponent(betCompaniesPanel)
                .addComponent(countriesAndLeaguesPanel));
        setPreferredSize(new Dimension(widthOfPanel, 400));
        setMaximumSize(new Dimension(widthOfPanel, 600));
        setLayout(gl);
    }
    
    private void initBetCompaniesPanel(){
        betCompaniesPanel = new JPanel();
        betCompaniesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Bet companies"));
        betCompaniesPanel.setPreferredSize(new Dimension(widthOfPanel, 100));
        betCompaniesPanel.setMaximumSize(new Dimension(widthOfPanel, 100));
    }
    
    private void initDatesPanel(){
        from = new DateChooser();
        by = new DateChooser();
        from.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
                bundle.getString("from")));
        by.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
                bundle.getString("by")));
        Dimension dim = new Dimension(150, 50);
        from.setPreferredSize(dim);
        by.setPreferredSize(dim);
        
        datesPanel = new JPanel();
        GroupLayout gl = new GroupLayout(datesPanel);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                    .addGap(10, 10, 100)
                    .addComponent(from)
                    .addGap(10, 10, 100)
                    .addComponent(by)
                    .addGap(10, 10, 100));
        gl.setVerticalGroup(gl.createParallelGroup(Alignment.CENTER)
                    .addComponent(from, 40, 40, 40)
                    .addComponent(by));
        gl.linkSize(from, by);
        datesPanel.setLayout(gl);
        datesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Dates"));
        datesPanel.setPreferredSize(new Dimension(widthOfPanel, 70));
        datesPanel.setMaximumSize(new Dimension(widthOfPanel, 70));
    }
    
    private void initCountriesAndLeaguesPanel(){
        selectedLeaguesModel = new DefaultListModel<>();
        selectedLeagues = new JList<>(selectedLeaguesModel);
        selectedLeagues.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectedLeagues.setPreferredSize(null);
        JScrollPane pane = new JScrollPane(selectedLeagues);
        pane.setPreferredSize(new Dimension(300, 100));
        pane.setMaximumSize(new Dimension(300, 100));
        availableLeagues = manager.getAvailableCountriesAndLeagues();
        String[] ctrs = addAllOption(availableLeagues.keySet());
        countries = new DefaultComboBoxModel<>(ctrs);
        countries.setSelectedItem(ctrs[0]);
        leagues = new DefaultComboBoxModel<>();
        putLeaguesAccordingToCountry();
        
        JComboBox<String> countriesCB = new JComboBox<>(countries);
        JComboBox<String> leaguesCB = new JComboBox<>(leagues);
        countriesCB.addActionListener(new CountrySelected());
        
        JButton add = new JButton("add");
        JButton remove = new JButton("remove");
        add.addActionListener(new AddingToListAction());
        remove.addActionListener(new RemovingFromListAction());
        
        countriesAndLeaguesPanel = new JPanel();
        countriesAndLeaguesPanel.setPreferredSize(new Dimension(widthOfPanel, 200));
        countriesAndLeaguesPanel.setMaximumSize(new Dimension(widthOfPanel, 200));
        GroupLayout gl = new GroupLayout(countriesAndLeaguesPanel);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
                .addGroup(gl.createParallelGroup(Alignment.CENTER)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(countriesCB, 180, 180, 180)
                        .addGroup(gl.createParallelGroup()
                            .addComponent(leaguesCB)))
                    .addGroup(gl.createSequentialGroup()
                        .addGap(10, 10, 100)
                        .addComponent(add)
                        .addGap(10, 10, 100)
                        .addComponent(remove)
                        .addGap(10, 10, 100)))
                .addGroup(gl.createParallelGroup(Alignment.CENTER, false)
                    .addComponent(pane)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup(Alignment.CENTER)
                        .addComponent(countriesCB, 20, 20, 20)
                        .addComponent(leaguesCB, 20, 20, 20))
                    .addGap(10, 10, 100)
                    .addGroup(gl.createParallelGroup(Alignment.CENTER)
                        .addComponent(add)
                        .addComponent(remove))
                    .addGap(10, 10, 100))
                .addGroup(gl.createParallelGroup(Alignment.CENTER, false)
                    .addComponent(pane)));
        
        gl.linkSize(add, remove);
        
        countriesAndLeaguesPanel.setLayout(gl);
        countriesAndLeaguesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Leagues"));
    }
    
    private void putLeaguesAccordingToCountry(){
        leagues.removeAllElements();
        String country = (String)countries.getSelectedItem();
        if(countries.getIndexOf(country) == 0){
            leagues.addElement("");
            return;
        }
        for(String s : addAllOption(availableLeagues.get(country))){
            leagues.addElement(s);
        }
        leagues.setSelectedItem("All");
    }
    
    private String[] addAllOption(Collection<String> col){
        String[] ar = col.toArray(new String[]{});
        String[] out = new String[ar.length + 1];
        Arrays.sort(ar);
        out[0] = "All";
        System.arraycopy(ar, 0, out, 1, ar.length);
        return out;
    }
    
    private class CountrySelected implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            putLeaguesAccordingToCountry();
        }
        
    }
    
    private class RemovingFromListAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] idxs = selectedLeagues.getSelectedIndices();
            Arrays.sort(idxs);
            for(int i = idxs.length-1; i >= 0; i--){
                selectedLeaguesModel.remove(idxs[i]);
            }
        }
        
    }
    
    private class AddingToListAction implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Tuple<String, String> toAdd = new Tuple<>(countries.getSelectedItem().toString(),
                    leagues.getSelectedItem().toString());
            for(int i = 0; i < selectedLeaguesModel.getSize(); i++){
                Tuple<String, String> tp = selectedLeaguesModel.getElementAt(i);
                if(tp.equals(toAdd)){
                    return;
                }
            }
            selectedLeaguesModel.addElement(toAdd);
        }
    }
    
}
