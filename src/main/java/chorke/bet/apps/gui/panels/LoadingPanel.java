
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.CoreUtils;
import chorke.bet.apps.core.Tuple;
import chorke.bet.apps.core.bets.Bet;
import chorke.bet.apps.core.match.Match;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Season;
import chorke.bet.apps.io.BetIOException;
import chorke.bet.apps.io.BetIOManager;
import chorke.bet.apps.io.LoadProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import org.chorke.gui.utils.panels.DateChooser;

/**
 *
 * @author Chorke
 */
public class LoadingPanel extends JPanel{
    
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
     * Panel pre výber krajín a líg.
     */
    private CountriesAndLeaguesPanel countriesAndLeaguesPanel;
    /**
     * Panel pre výber počiatočného a koncového dátumu.
     */
    private JPanel datesPanel;
    /**
     * Panel so stávkovými spoločnosťami.
     */
    private BetCompaniesListPanel betCompaniesPanel;
    /**
     * Tlačítko pre načítanie zápasov.
     */
    private JButton loadButton;
    
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
     * @throws BetIOException ak nastane nejaká chyba pri inicializácii okna
     * a čítaní niektorých hodnôt z DB
     */
    public LoadingPanel(Season season) throws IllegalArgumentException, BetIOException{
        if(season == null){
            throw new IllegalArgumentException("Season cannot be null.");
        }
        this.bundle = season.getDefaultBundle();
        this.manager = season.getManager();
        this.season = season;
        init();
    }
    
    /**
     * Inicializuje panel.
     */
    private void init(){
        initCountriesAndLeaguesPanel();
        initDatesPanel();
        initBetCompaniesPanel();
        
        int w = 450;
        
        loadButton = new JButton(bundle.getString("load"));
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
     * Pripraví panel so stávkovými spoločnosťami.
     */
    private void initBetCompaniesPanel(){
        Map<Class<? extends Bet>, Collection<String>> betCompanies = 
                CoreUtils.getAllAvailableBetCompanies(manager, GuiUtils.SUPPORTED_BET_TYPES);
        Class<? extends Bet> initBetClass = null;
        Collection<String> initBetComps = null;
        LoadProperties prop = season.getLoadProperties();
        if(prop != null){
            Set<Class<Bet>> classes = prop.getBetClasses();
            int initIdx = 0;
            if(classes != null && !classes.isEmpty()){
                initIdx = GuiUtils.getIndexOfClassInSupportedClasses(
                        classes.iterator().next());
            }
            initIdx = initIdx >= 0 ? initIdx : 0;
            initBetClass = GuiUtils.SUPPORTED_BET_TYPES[initIdx];
            Set<String> companies = prop.getBetCompanies();
            if(companies != null && !companies.isEmpty()){
                initBetComps = new HashSet<>(companies);
            }
        }
        betCompaniesPanel = new BetCompaniesListPanel(betCompanies,
                initBetClass, initBetComps, season);
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
        datesPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
                bundle.getString("dates")));
    }
    
    /**
     * Inicializuje panel pre výber krajín a líg {@link #countriesAndLeaguesPanel}.
     */
    private void initCountriesAndLeaguesPanel(){
        Set<Tuple<String, String>> lgs = null;
        if(season.getLoadProperties() != null){
            lgs = season.getLoadProperties().getLeagues();
        }
        countriesAndLeaguesPanel = new CountriesAndLeaguesPanel(
                manager.getAvailableCountriesAndLeagues(), lgs, season);
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
            JButton cancel = new JButton(bundle.getString("cancel"));
            cancel.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    manager.cancelActualQuery();
                    ((JButton)e.getSource()).setEnabled(false);
                }
            });
            GuiUtils.showWaitingDialog(bundle.getString("loading"), cancel);
            errors.clear();
            LoadProperties prop = getLoadProperties();
            if(checkLoadingProperties(prop)){
                if(season.getMatches() != null){
                    season.getMatches().clear();
                }
                return new Tuple<>(prop, manager.loadMatches(prop));
            } else {
                StringBuilder message = new StringBuilder(bundle.getString("badProperties"))
                        .append(System.lineSeparator());
                for(String err : errors){
                    message.append(err).append(System.lineSeparator());
                }
                JOptionPane.showOptionDialog(null, message, bundle.getString("badProperties"), 
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
                    JOptionPane.showMessageDialog(null, bundle.getString("loadingSuccessful"));
                }
            } catch (ExecutionException | InterruptedException | CancellationException ex){
                if(ex.getCause() instanceof Error){
                    System.gc();
                }
                System.out.println(ex);
                JOptionPane.showMessageDialog(null, bundle.getString("loadingFailed"));
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
            if(prop.getBetCompanies() == null || prop.getBetCompanies().isEmpty()){
                errors.add(bundle.getString("noBetComp"));
            }
            if(prop.getLeagues() == null || prop.getLeagues().isEmpty()){
                errors.add(bundle.getString("noLeagues"));
            }
            return errors.isEmpty();
        }    

        /**
         * Vytvorí z aktuálnych nastavení LoadingProperties.
         * @return 
         */
        private LoadProperties getLoadProperties() {
            LoadProperties prop = new LoadProperties();
            Calendar firstDate = manager.getFirstDate();
            Calendar lastDate = manager.getLastDate();
            if(by.getActualDate().before(from.getActualDate())){
                errors.add(bundle.getString("endBeforeStart"));
            }
            if(!CoreUtils.areSameDates(from.getActualDate(), firstDate)
                    && !from.getActualDate().before(firstDate)){
                prop.addStartDate(from.getActualDate());
            }
            if(!CoreUtils.areSameDates(by.getActualDate(), lastDate)
                    && !by.getActualDate().after(lastDate)){
                prop.addEndDate(by.getActualDate());
            }
            prop.addBetClass(betCompaniesPanel.getActualChosenBetType());
            for(String s : betCompaniesPanel.getActualChosenBetCompanies()){
                prop.addBetCompany(s);
            }
            Map<String, Set<String>> leagues = countriesAndLeaguesPanel.getChoosenLeagues();
            for(String ctr : leagues.keySet()){
                for(String lgs : leagues.get(ctr)){
                    prop.addLeague(ctr, lgs);
                }
            }
            return prop;
        }
    }
}