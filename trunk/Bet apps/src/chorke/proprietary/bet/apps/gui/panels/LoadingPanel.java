
package chorke.proprietary.bet.apps.gui.panels;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.gui.GuiUtils;
import chorke.proprietary.bet.apps.gui.Season;
import chorke.proprietary.bet.apps.io.BetIOManager;
import chorke.proprietary.bet.apps.io.LoadProperties;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Chorke
 */
public class LoadingPanel extends JPanel{

    /**
     * Podporované typy stávok.
     */
    private Class[] supportedBetsType = GuiUtils.SUPPORTED_BET_TYPES;
    
    /**
     * Sedenie, kde sa uložia zápasy a LoadProperties po načítaní.
     * Ak season už obsahuje už nastavené LoadProperties, tak okno je
     * nastavené podľa nich.
     */
    private Season season;
    
    /**
     * Počiatočný dátum.
     */
    private DateChooser from;
    /**
     * Koncový dátum.
     */
    private DateChooser by;
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
     * CheckBox tlačítka pre dostupné stávkové spoločnosti. 
     */
    private JCheckBox[] betCompaniesButtons;
    /**
     * Dostupné stávkové spoločnosti v DB. Ukladaný formát je (typ stávky,
     * zoznam spoločností).
     */
    private Map<Class, Collection<String>> betCompanies;
    
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
     * Panel pre výber krajín a líg.
     */
    private JPanel countriesAndLeaguesPanel;
    /**
     * Panel pre výber počiatočného a koncového dátumu.
     */
    private JPanel datesPanel;
    /**
     * Panel, ktoré obsahuje stávkové spoločnosti aj tlačítka pre možné typy 
     * stávok.
     */
    private JPanel betCompaniesPanel;
    /**
     * Panel, ktorý obsahuje zoznam aktuálne zvolených líg.
     */
    private JPanel betCompaniesList;
    
    /**
     * Akutálne zvolený požedovaný typ stávky.
     */
    private Class betClassRequired;
    
    /**
     * Tlačítko pre pridávanie líg.
     */
    private JButton addButton;
    /**
     * Tlačítko pre odstraňovanie líg.
     */
    private JButton removeButton;
    /**
     * Tlačítko pre načítanie zápasov.
     */
    private JButton loadButton;
    /**
     * Skupina tlačítok, ktoré slúžia pre zvolenie typu stávky. 
     */
    private JRadioButton[] betTypeButtons;
    
    /**
     * Možnosť, ktorá značí zvolenie všetkých krajín, prípadne líg. 
     */
    private String allOption = "All";
    
    /**
     * Manažér pre načítavanie stávok a získavanie informácii o stávkach z DB.
     */
    private BetIOManager manager;
    
    /**
     * ResourceBundle pre lokalizáciu a i18n.
     */
    private ResourceBundle bundle;
    
    /**
     * Vytvorí novú inštanciu. 
     * 
     * @param season 
     * @throws IllegalArgumentException ak je season == null
     */
    public LoadingPanel(Season season) throws IllegalArgumentException{
        if(season == null){
            throw new IllegalArgumentException("Season cannot be null.");
        }
        this.bundle = season.getDefaultBundle();
        this.manager = season.getManager();
        this.season = season;
        GuiUtils.showWaitingDialog("Inicializing loading");
        init();
        GuiUtils.hideWaitingDialog();
    }
    
    /**
     * Inicializuje panel.
     */
    private void init(){
        initCountriesAndLeaguesPanel();
        initDatesPanel();
        initBetCompaniesPanel();
        
        int w = 450;
        
        loadButton = new JButton("load");
        loadButton.addActionListener(new LoadAction());
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createParallelGroup(Alignment.CENTER)
                .addComponent(datesPanel, w, w, w)
                .addComponent(betCompaniesPanel, w, w, w)
                .addComponent(countriesAndLeaguesPanel, w, w, w)
                .addGroup(gl.createParallelGroup(Alignment.CENTER)
                    .addComponent(loadButton, 150, 150, 150)));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(datesPanel)
                .addGap(10, 10, 30)
                .addComponent(betCompaniesPanel, 190, 190, 220)
                .addGap(10, 10, 30)
                .addComponent(countriesAndLeaguesPanel)
                .addGap(10, 10, 30)
                .addComponent(loadButton, 30, 30, 30));
        setPreferredSize(new Dimension(w + 20, 620));
        setMaximumSize(new Dimension(w + 20, 900));
        setLayout(gl);
    }
    
    /**
     * Inicializuje panel pre stávkové spoločnosti {@link #betCompaniesPanel}.
     */
    private void initBetCompaniesPanel(){
        betCompaniesPanel = new JPanel();
        betCompaniesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Bet companies"));
        
        betCompanies = new HashMap<>();
        
        for(Class c : supportedBetsType){
            betCompanies.put(c, manager.getAvailableBetCompanies(c));
        }
        
        int initIdx = 0;
        if(season.getLoadProperties() != null){
            Set<Class<Bet>> set = season.getLoadProperties().getBetClasses();
            if(set != null && !set.isEmpty()){
                initIdx = GuiUtils.getIndexOfClassInSupportedClasses(
                        set.iterator().next());
            }
        }
        boolean betClassSet = initIdx >= 0;
        initIdx = initIdx >= 0 ? initIdx : 0;
        betClassRequired = supportedBetsType[initIdx];
        
        betTypeButtons = new JRadioButton[supportedBetsType.length];
        int i = 0;
        for(Class c : supportedBetsType){
            betTypeButtons[i] = GuiUtils.getRadioButton(c.getSimpleName(),
                    new BetTypeSet(c));
            if(i == initIdx){
                betTypeButtons[i].setSelected(true);
            }
            i++;
        }
        GuiUtils.mergeToOneButtonGroup(betTypeButtons);
        
        JPanel betPossPanel = new JPanel();
        betPossPanel.setLayout(new GridLayout(
                GuiUtils.getNumOfRows(2, supportedBetsType.length), 2));
        for(JRadioButton b : betTypeButtons){
            betPossPanel.add(b);
        }
        
        betCompaniesList = new JPanel();
        betCompaniesList.setLayout(new BorderLayout());
        putProperBetCompanies();
        if(betClassSet && season.getLoadProperties() != null){
            Set<String> betCp = season.getLoadProperties().getBetCompanies();
            if(betCp != null){
                for(JCheckBox cb : betCompaniesButtons){
                    if(betCp.contains(cb.getText())){
                        cb.setSelected(true);
                    }
                }
            }
        }
        GroupLayout gl = new GroupLayout(betCompaniesPanel);
        gl.setAutoCreateGaps(true);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(betPossPanel)
                .addComponent(betCompaniesList));
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(betPossPanel)
                .addComponent(betCompaniesList));
        
        betCompaniesPanel.setLayout(gl);
    }
    
    /**
     * Nastaví stávkové spoločosti pre {@link #betCompaniesList} podľa aktuálne
     * zvoleného typu stávky.
     */
    private void putProperBetCompanies(){
        betCompaniesList.removeAll();
        betCompaniesList.add(prepareBetCompaniesList());
        betCompaniesList.paintAll(betCompaniesList.getGraphics());
    }
    
    /**
     * Vráti JScrollPane s panelom, ktorý obsahuje dostupné stávkové spoločnosti
     * podľa aktuálne zvoleného typu stávky. 
     * @return 
     */
    private JScrollPane prepareBetCompaniesList(){
        Collection<String> comp = betCompanies.get(betClassRequired);
        betCompaniesButtons = new JCheckBox[comp.size()];
        int i = 0;
        for(String s : comp){
            betCompaniesButtons[i] = new JCheckBox(s);
            betCompaniesButtons[i++].setBackground(Color.WHITE);
        }
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(GuiUtils.getNumOfRows(3, betCompaniesButtons.length), 3));
        for(JCheckBox cb : betCompaniesButtons){
            pan.add(cb);
        }
        pan.setBackground(Color.WHITE);
        JScrollPane out = new JScrollPane(pan);
        out.getVerticalScrollBar().setUnitIncrement(10);
        return out;
    }
    
    /**
     * Inicializuje panel pre dátumy {@link #datesPanel}.
     */
    private void initDatesPanel(){
        from = new DateChooser(season.getDefaultLocale());
        by = new DateChooser(season.getDefaultLocale());
        from.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
                bundle.getString("from")));
        by.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),
                bundle.getString("by")));
        Calendar c = season.getLoadProperties() == null ? null 
                : season.getLoadProperties().getStartDate();
        from.setActualDate(c == null ? manager.getFirstDate() : c);
        c = season.getLoadProperties() == null ? null 
                : season.getLoadProperties().getEndDate();
        by.setActualDate(c == null ? manager.getLastDate() : c);
        Dimension dim = new Dimension(150, 50);
        from.setPreferredSize(dim);
        by.setPreferredSize(dim);
        
        datesPanel = new JPanel();
        GroupLayout gl = new GroupLayout(datesPanel);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                    .addGap(10, 10, 100)
                    .addComponent(from)
                    .addGap(10, 10, 100)
                    .addComponent(by)
                    .addGap(10, 10, 100));
        gl.setVerticalGroup(gl.createParallelGroup(Alignment.CENTER)
                    .addGap(10, 10, 20)
                    .addComponent(from, 40, 40, 40)
                    .addGap(10, 10, 20)
                    .addComponent(by)
                    .addGap(10, 10, 20));
        gl.linkSize(from, by);
        datesPanel.setLayout(gl);
        datesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Dates"));
    }
    
    /**
     * Inicializuje panel pre výber krajín a líg {@link #countriesAndLeaguesPanel}.
     */
    private void initCountriesAndLeaguesPanel(){
        selectedLeaguesModel = new DefaultListModel<>();
        if(season.getLoadProperties() != null){
            Set<Tuple<String, String>> lgs = season.getLoadProperties().getLeagues();
            if(lgs != null){
                for(Tuple<String, String> tp : lgs){
                    String c = (tp.first == null || tp.first.isEmpty())
                            ? allOption : tp.first;
                    String l = (tp.second == null || tp.second.isEmpty())
                            ? allOption : tp.second;
                    selectedLeaguesModel.addElement(new Tuple<>(c, l));
                }
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
        availableLeagues = manager.getAvailableCountriesAndLeagues();
        String[] ctrs = addAllOption(availableLeagues.keySet());
        countries = new DefaultComboBoxModel<>(ctrs);
        countries.setSelectedItem(ctrs[0]);
        leagues = new DefaultComboBoxModel<>();
        putLeaguesAccordingToCountry();
        
        JComboBox<String> countriesCB = new JComboBox<>(countries);
        JComboBox<String> leaguesCB = new JComboBox<>(leagues);
        countriesCB.addActionListener(new CountrySelected());
        
        addButton = new JButton("add");
        removeButton = new JButton("remove");
        removeButton.setEnabled(false);
        addButton.addActionListener(new AddingToListAction());
        removeButton.addActionListener(new RemovingFromListAction());
        
        countriesAndLeaguesPanel = new JPanel();
        
        JLabel countryLabel = new JLabel("Country");
        JLabel leagueLabel = new JLabel("League");
        GroupLayout gl = new GroupLayout(countriesAndLeaguesPanel);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(countryLabel)
                    .addComponent(leagueLabel))
                .addGroup(gl.createParallelGroup(Alignment.CENTER)
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
        countriesAndLeaguesPanel.setLayout(gl);
        countriesAndLeaguesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Leagues"));
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
    
    /**
     * Akcia, ktorá nastaví stávkové spoločnosti. Spoločnosti tú nastavené podľa
     * argumentu {@link #cls}, ktorý je predávaný pri konštruktore.
     */
    private class BetTypeSet implements ActionListener {

        private Class cls;

        public BetTypeSet(Class cls) {
            this.cls = cls;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!(betClassRequired == cls)){
                betClassRequired = cls;
                putProperBetCompanies();
            }
        }
    }
    
    /**
     * Akcia pre načítanie zápasov podľa aktuálne nastavených parametrov.
     */
    private class LoadAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            LoadingWorker worker = new LoadingWorker();
            worker.execute();
        }
    }
    
    /**
     * Worker, ktorý načíta požadované západy. 
     */
    private class LoadingWorker extends SwingWorker
            <Tuple<LoadProperties, Collection<Match>>, Void>{

        /**
         * Chyby pri nastavovaní loading properties.
         */
        private Collection<String> errors = new LinkedList<>();
        
        @Override
        protected Tuple<LoadProperties, Collection<Match>> doInBackground() throws Exception {
            GuiUtils.showWaitingDialog("loading");
            LoadProperties prop = getLoadProperties();
            if(checkLoadingProperties(prop)){
                season.getMatches().clear();
                return new Tuple<>(prop, manager.loadMatches(prop));
            } else {
                StringBuilder message = new StringBuilder("Bad properties")
                        .append(System.lineSeparator());
                for(String err : errors){
                    message.append(err).append(System.lineSeparator());
                }
                JOptionPane.showOptionDialog(null, message, "Bad properties", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, null, null);
                return new Tuple<>(prop, (Collection<Match>)new LinkedList<Match>());
            }
        }

        @Override
        protected void done() {
            try{
                Tuple<LoadProperties, Collection<Match>> m = get();
                if(errors.isEmpty()){
                    season.setLoadProperties(m.first);
                    season.setMatches(m.second);
                }
                System.out.println(season.getMatches().size());
            } catch (ExecutionException | InterruptedException ex){
                System.out.println(ex);
            }
            GuiUtils.hideWaitingDialog();
        }
        
        /**
         * Skontorluje, či sú properties dobre nastavené. Ak áno vráti true,
         * inak vráti false. Prípadné chyby nastaví do {@link #errors}.
         * 
         * @param prop
         * @return 
         */
        private boolean checkLoadingProperties(LoadProperties prop){
            System.out.println("checking load prop");
            errors.clear();
            if(prop.getEndDate().before(prop.getStartDate())){
                errors.add("End date before start date.");
            }
            Set s = prop.getBetCompanies();
            if(s == null || s.isEmpty()){
                errors.add("No bet companies.");
            }
            s = prop.getLeagues();
            if(s == null || s.isEmpty()){
                errors.add("No leagues");
            }
            return errors.isEmpty();
        }    

        /**
         * Vytvorí z aktuálnych nastavení LoadingProperties.
         * @return 
         */
        private LoadProperties getLoadProperties() {
            System.out.println("getting load prop");
            LoadProperties prop = new LoadProperties();
            prop.addStartDate(from.getActualDate());
            prop.addEndDate(by.getActualDate());
            prop.addBetClass(betClassRequired);
            for(JCheckBox cb : betCompaniesButtons){
                if(cb.isSelected()){
                    prop.addBetCompany(cb.getText());
                }
            }
            Map<String, Set<String>> leagues = new HashMap<>();
            for(Enumeration<Tuple<String, String>> e = selectedLeaguesModel.elements();
                    e.hasMoreElements();){
                Tuple<String, String> el = e.nextElement();
                if(leagues.containsKey(el.first)){
                    ((Set<String>)leagues.get(el.first)).add(el.second);
                } else {
                    Set<String> set = new HashSet<>();
                    set.add(el.second);
                    leagues.put(el.first, set);
                }
            }
            //removing unnecessary records
            if(leagues.containsKey(allOption)){ // required all matches
                prop.addLeague("", "");
            } else {
                for(String s : leagues.keySet()){
                    Set<String> set = (Set<String>)leagues.get(s);
                    if(set.contains(allOption)){ // required all leagues of country
                        set.clear();
                        set.add("");
                    }
                }
                for(String c : leagues.keySet()){
                    for(String l : (Set<String>)leagues.get(c)){
                        prop.addLeague(c, l);
                    }
                }
            }
            return prop;
        }
    }
}