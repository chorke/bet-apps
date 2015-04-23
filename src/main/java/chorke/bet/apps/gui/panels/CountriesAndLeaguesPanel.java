
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.Tuple;
import chorke.bet.apps.gui.Season;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Chorke
 */
public class CountriesAndLeaguesPanel extends JPanel{
    
    /**
     * ResourceBundle pre lokalizáciu a i18n.
     */
    private ResourceBundle bundle;
    
    /**
     * Možnosť, ktorá značí zvolenie všetkých krajín, prípadne líg. 
     */
    private String allOption;
    
    /**
     * Tlačítko pre pridávanie líg.
     */
    private JButton addButton;
    /**
     * Tlačítko pre odstraňovanie líg.
     */
    private JButton removeButton;
    /**
     * Zoznam, ktorý obsahuje zvolené ligy. Tvar ukladania je (krajina, liga).
     */
    private JList<Tuple<String, String>> selectedLeagues;
    /**
     * ListModel pre zoznam požadovaných líg (skrze neho je možné
     * pridávať a odstraňovať položky zoznamu).
     */
    private DefaultListModel<Tuple<String, String>> selectedLeaguesModel;
    /**
     * Dostupné ligy v DB. Ukladný formát je (krajina, ligy v krajine).
     */
    private Map<String, Collection<String>> availableLeagues;
    
    /**
     * ComboBoxModel pre zoznam krajín.
     */
    private DefaultComboBoxModel<String> countries;
    /**
     * ComboBoxModel pre zoznam líg (aktuálne sa nastaví jeho obsah podľa práve
     * zvolenej krajiny z {@link #countries}.
     */
    private DefaultComboBoxModel<String> leagues;
    
    /**
     * Vytvorý nový panel pre ligy a krajiny.
     * 
     * @param availableLeagues všetky dostupné krajiny a k nim dostupné ligy
     * @param initialChoosenLeagues ligy, ktoré majú byť iniciálne zvolené
     * @param season aktuálne sedenie (pre získanie {@link ResourceBundle})
     */
    public CountriesAndLeaguesPanel(Map<String, Collection<String>> availableLeagues,
            Set<Tuple<String, String>> initialChoosenLeagues,
            Season season){
        if(availableLeagues == null){
            throw new IllegalArgumentException("Available leagues are null");
        }
        this.availableLeagues = availableLeagues;
        bundle = season.getDefaultBundle();
        allOption = bundle.getString("all");
        init(initialChoosenLeagues);
    }
    
    /**
     * Inicializuje panel.
     * 
     * @param initialLeagues ligy, ktoré majú byť iniciálne vybrané.
     */
    private void init(Set<Tuple<String, String>> initialLeagues){
        selectedLeaguesModel = new DefaultListModel<>();
        if(initialLeagues != null){
            for(Tuple<String, String> tp : initialLeagues){
                String c = (tp.first == null || tp.first.isEmpty())
                        ? allOption : tp.first;
                String l = (tp.second == null || tp.second.isEmpty())
                        ? allOption : tp.second;
                selectedLeaguesModel.addElement(new Tuple<>(c, l));
            }
        }
        selectedLeagues = new JList<>(selectedLeaguesModel);
        selectedLeagues.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectedLeagues.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    if(selectedLeagues.getSelectedIndices().length == 0){
                        removeButton.setEnabled(false);
                    } else {
                        removeButton.setEnabled(true);
                    }
                }
            }
        });
        JScrollPane pane = new JScrollPane(selectedLeagues);
        String[] ctrs = addAllOption(availableLeagues.keySet());
        countries = new DefaultComboBoxModel<>(ctrs);
        countries.setSelectedItem(ctrs[0]);
        leagues = new DefaultComboBoxModel<>();
        putLeaguesAccordingToCountry();
        
        JComboBox<String> countriesCB = new JComboBox<>(countries);
        JComboBox<String> leaguesCB = new JComboBox<>(leagues);
        countriesCB.addActionListener(new CountrySelected());
        
        addButton = new JButton(bundle.getString("add"));
        removeButton = new JButton(bundle.getString("remove"));
        removeButton.setEnabled(false);
        addButton.addActionListener(new AddingToListAction());
        removeButton.addActionListener(new RemovingFromListAction());
        
        JLabel countryLabel = new JLabel(bundle.getString("country"));
        JLabel leagueLabel = new JLabel(bundle.getString("league"));
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(countryLabel)
                    .addComponent(leagueLabel))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addGroup(gl.createParallelGroup()
                        .addComponent(countriesCB)
                        .addComponent(leaguesCB))
                    .addGroup(gl.createSequentialGroup()
                        .addGap(10, 40, 200)
                        .addComponent(addButton)
                        .addGap(10, 40, 200)
                        .addComponent(removeButton)
                        .addGap(10, 40, 200))
                    .addComponent(pane)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(countryLabel)
                        .addComponent(leagueLabel))
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(countriesCB)
                        .addComponent(leaguesCB)))
                .addGroup(gl.createParallelGroup()
                    .addComponent(addButton)
                    .addComponent(removeButton))
                .addComponent(pane));
        
        gl.linkSize(addButton, removeButton);
        gl.linkSize(SwingConstants.VERTICAL, countriesCB, countryLabel, leagueLabel, leaguesCB);
        setLayout(gl);
        setBorder(new TitledBorder(new LineBorder(Color.BLACK),
                bundle.getString("leagues")));
    }
    
    /**
     * Nastaví pre {@link #leagues} ligy, ktoré zodpovedajú aktuálne vybranej 
     * krajine v {@link #countries}.
     */
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
        leagues.setSelectedItem(allOption);
    }
    
    /**
     * Vytvorí pole reťazcov, z kolekcie col. Výsledné pole bude obsahovať
     * ako prvý prvok {@link #allOption} a za ním budú následovať prvky z
     * col tak, ako ich vráti {@link Collection#toArray(T[])}.
     * @param col
     * @return 
     */
    private String[] addAllOption(Collection<String> col){
        String[] ar = col.toArray(new String[]{});
        String[] out = new String[ar.length + 1];
        Arrays.sort(ar);
        out[0] = allOption;
        System.arraycopy(ar, 0, out, 1, ar.length);
        return out;
    }
    
    /**
     * Vráti zvolené ligy. Kľúč v mape je krajiny a k nej zvolené ligy.
     * Prázdny reťazec znamená, že sú zvolené všetky ligy, prípadne krajiny.
     * 
     * @return 
     */
    public Map<String, Set<String>> getChoosenLeagues(){
        Map<String, Set<String>> allLeagues = new HashMap<>();
        for(Enumeration<Tuple<String, String>> e = selectedLeaguesModel.elements();
                    e.hasMoreElements();){
            Tuple<String, String> el = e.nextElement();
            if(allLeagues.containsKey(el.first)){
                allLeagues.get(el.first).add(el.second);
            } else {
                Set<String> set = new HashSet<>();
                set.add(el.second);
                allLeagues.put(el.first, set);
            }
        }
        String all = "";
        //removing unnecessary records
        if(allLeagues.containsKey(allOption)){ // required all matches
            allLeagues.clear();
            allLeagues.put(all, new HashSet<>(Arrays.asList(all)));
        } else {
            for(String ctr : allLeagues.keySet()){
                Set<String> lgs = allLeagues.get(ctr);
                if(lgs.contains(allOption)){  // required all leagues of country
                    lgs.clear();
                    lgs.add(all);
                }
            }
        }
        return allLeagues;
    }
    
    /**
     * Akcia, ktorá má byť vykonaná pri výbere krajiny.
     * Konkrétne {@link #putLeaguesAccordingToCountry()}.
     */
    private class CountrySelected implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            putLeaguesAccordingToCountry();
        }
        
    }
    
    /**
     * Akcia, ktorá odstráni zo zoznami vybraných líg a krajíny
     * {@link #selectedLeagues}, resp. {@link #selectedLeaguesModel} 
     * aktuálne vybrané položky.
     */
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
    
    /**
     * Akcia, ktorá pridá do zoznami vybraných líg a krajíny
     * {@link #selectedLeagues}, resp. {@link #selectedLeaguesModel} 
     * aktuálne vybrané položky v {@link #countries} a {@link #leagues}.
     */
    private class AddingToListAction implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String l = leagues.getSelectedItem().toString();
            Tuple<String, String> toAdd = new Tuple<>(countries.getSelectedItem().toString(),
                    l.isEmpty() ? allOption : l);
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
