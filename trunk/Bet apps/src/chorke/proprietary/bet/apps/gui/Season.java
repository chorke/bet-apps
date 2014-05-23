
package chorke.proprietary.bet.apps.gui;

import chorke.proprietary.bet.apps.StaticConstants;
import chorke.proprietary.bet.apps.core.calculators.YieldCalculator;
import chorke.proprietary.bet.apps.core.calculators.YieldProperties;
import chorke.proprietary.bet.apps.core.graphs.GraphBuilder;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.io.CloneableBetIOManager;
import chorke.proprietary.bet.apps.io.IOTransferable;
import chorke.proprietary.bet.apps.io.LoadProperties;
import chorke.proprietary.bet.apps.users.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Trieda pre informácie a dáta používané aktuálnym užívateľom aplikácie.
 * 
 * @author Chorke
 */
public class Season implements IOTransferable{
    
    private LoadProperties loadProperties;
    private Collection<Match> matches;
    
    private YieldCalculator calculator;
    private YieldProperties yieldProperties;

    private HTMLBetParser parser;
    private CloneableBetIOManager manager;
    
    private GraphBuilder graphBuilder;
    
    private User user;
    
    /**
     * Akcie, ktoré budú vykonané po nastavení LoadProperties
     */
    private Set<Action> actionsAfterLoadPropertiesSet = new HashSet<>();
    /**
     * Akcie, ktoré budú vykonané po nastavení matches.
     */
    private Set<Action> actionsAfterMatchesSet = new HashSet<>();
    
    /**
     * Akcie, ktoré budú vykonané po nastavení YieldCalculator.
     */
    private Set<Action> actionsAfterCalculatorSet = new HashSet<>();
    /**
     * Akcie, ktoré budú vykonané po nastavení YieldProperties.
     */
    private Set<Action> actionsAfterYieldPropertiesSet = new HashSet<>();
    
    /**
     * Akcie, ktoré budú vykonané po nastavení HTMLBetParser.
     */
    private Set<Action> actionsAfterParserSet = new HashSet<>();
    /**
     * Akcie, ktoré budú vykonané po nastavení BetIOManager.
     */
    private Set<Action> actionsAfterManagerSet = new HashSet<>();
    
    /**
     * Akcie, ktoré budú vykonané po nastavení GraphBuilder.
     */
    private Set<Action> actionsAfterGraphBuilderSet = new HashSet<>();

    /**
     * Pridá ďalšiu akciu, ktorá má byť vykonaná po nastavení LoadProperties.
     */
    public void addActionAfterLoadPropertiesSet(Action a){
        actionsAfterLoadPropertiesSet.add(a);
    }
    
    /**
     * Pridá ďalšiu akciu, ktorá má byť vykonaná po nastavení Matches.
     */
    public void addActionAfterMatchesSet(Action a){
        actionsAfterMatchesSet.add(a);
    }
    
    /**
     * Pridá ďalšiu akciu, ktorá má byť vykonaná po nastavení YieldCalculator.
     */
    public void addActionAfterCalculatorSet(Action a){
        actionsAfterCalculatorSet.add(a);
    }
    
    /**
     * Pridá ďalšiu akciu, ktorá má byť vykonaná po nastavení YieldProperties.
     */
    public void addActionAfterYieldPropertiesSet(Action a){
        actionsAfterYieldPropertiesSet.add(a);
    }
    
    /**
     * Pridá ďalšiu akciu, ktorá má byť vykonaná po nastavení HTMLBetParser.
     */
    public void addActionAfterParserSet(Action a){
        actionsAfterParserSet.add(a);
    }
    
    /**
     * Pridá ďalšiu akciu, ktorá má byť vykonaná po nastavení Manager.
     */
    public void addActionAfterManagerSet(Action a){
        actionsAfterManagerSet.add(a);
    }
    
    /**
     * Pridá ďalšiu akciu, ktorá má byť vykonaná po nastavení GraphBuilder.
     */
    public void addActionAfterGraphBuilderSet(Action a){
        actionsAfterGraphBuilderSet.add(a);
    }
    
    /**
     * Odtráni akciu zo zoznamu akcií, ktoré majú byť vykonané po nastavení
     * LoadProperties.
     */
    public void removeActionAfterLoadPropertiesSet(Action a){
        actionsAfterLoadPropertiesSet.remove(a);
    }
    
    /**
     * Odtráni akciu zo zoznamu akcií, ktoré majú byť vykonané po nastavení
     * Matches.
     */
    public void removeActionAfterMatchesSet(Action a){
        actionsAfterMatchesSet.remove(a);
    }
    
    /**
     * Odtráni akciu zo zoznamu akcií, ktoré majú byť vykonané po nastavení
     * YieldCalculator.
     */
    public void removeActionAfterCalculatorSet(Action a){
        actionsAfterCalculatorSet.remove(a);
    }
    
    /**
     * Odtráni akciu zo zoznamu akcií, ktoré majú byť vykonané po nastavení
     * YieldProperties.
     */
    public void removeActionAfterYieldPropertiesSet(Action a){
        actionsAfterYieldPropertiesSet.remove(a);
    }
    
    /**
     * Odtráni akciu zo zoznamu akcií, ktoré majú byť vykonané po nastavení
     * HTMLBetParser.
     */
    public void removeActionAfterParserSet(Action a){
        actionsAfterParserSet.remove(a);
    }
    
    /**
     * Odtráni akciu zo zoznamu akcií, ktoré majú byť vykonané po nastavení
     * BetIOManager.
     */
    public void removeActionAfterManagerSet(Action a){
        actionsAfterManagerSet.remove(a);
    }
    
    /**
     * Odtráni akciu zo zoznamu akcií, ktoré majú byť vykonané po nastavení
     * GraphBuilder.
     */
    public void removeActionAfterGraphBuilderSet(Action a){
        actionsAfterGraphBuilderSet.remove(a);
    }
    
    /**
     * Vráti aktuálne nastavený GraphBuilder.
     * @return 
     */
    public GraphBuilder getGraphBuilder() {
        return graphBuilder;
    }

    /**
     * Nastaví GraphBuilder.
     * @param graphBuilder 
     */
    public void setGraphBuilder(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
        doActions(actionsAfterGraphBuilderSet);
    }
    
    /**
     * Vráti defaultné Locale aplikácie.
     * @return 
     */
    public Locale getDefaultLocale(){
        return StaticConstants.getDefaultLocale();
    }
    
    /**
     * Vráti defaultné ResourceBundle aplikácie.
     * @return 
     */
    public ResourceBundle getDefaultBundle(){
        return StaticConstants.BUNDLE;
    }
    /**
     * Vráti aktuálne nastavené LoadProperties.
     * @return 
     */
    public LoadProperties getLoadProperties() {
        return loadProperties;
    }

    /**
     * Nastaví LoadProperties.
     * @param loadProperties 
     */
    public void setLoadProperties(LoadProperties loadProperties) {
        if(this.loadProperties != null){
            this.loadProperties.clear();
        }
        this.loadProperties = loadProperties;
        doActions(actionsAfterLoadPropertiesSet);
    }

    /**
     * Vráti aktuálne načítané zápasy.
     * @return 
     */
    public Collection<Match> getMatches() {
        return matches;
    }

    /**
     * Nastaví zápasy.
     * @param matches 
     */
    public void setMatches(Collection<Match> matches) {
        if(this.matches != null){
            this.matches.clear();
        }
        this.matches = matches;
        doActions(actionsAfterMatchesSet);
    }

    /**
     * Vráti YieldCalculator pre toto sedenie.
     * @return 
     */
    public YieldCalculator getCalculator() {
        return calculator;
    }

    /**
     * Nastaví YieldCalculator.
     * @param calculator 
     */
    public void setCalculator(YieldCalculator calculator) {
        this.calculator = calculator;
        doActions(actionsAfterCalculatorSet);
    }

    /**
     * Vráti akutálne nastavené YieldProperties.
     * @return 
     */
    public YieldProperties getYieldProperties() {
        return yieldProperties;
    }

    /**
     * Nastaví YieldProperties.
     * @param yieldProperties 
     */
    public void setYieldProperties(YieldProperties yieldProperties) {
        this.yieldProperties = yieldProperties;
        doActions(actionsAfterYieldPropertiesSet);
    }

    /**
     * Nastaví parser.
     * @param parser 
     */
    public void setParser(HTMLBetParser parser) {
        this.parser = parser;
        doActions(actionsAfterParserSet);
    }

    /**
     * Vráti aktuálne nastavený parser.
     * @return 
     */
    public HTMLBetParser getParser() {
        return parser;
    }

    /**
     * Nastaví manager.
     * @param manager 
     */
    public void setManager(CloneableBetIOManager manager) {
        this.manager = manager;
        doActions(actionsAfterManagerSet);
    }

    /**
     * Vráti aktuálne nastavený manager.
     * @return 
     */
    public CloneableBetIOManager getManager() {
        return manager;
    }

    @Override
    public void save(BufferedWriter stream) throws IOException {}

    @Override
    public void load(BufferedReader stream) throws IOException {}

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Season: ").append(System.lineSeparator())
                .append(matches).append(System.lineSeparator())
                .append(loadProperties).append(System.lineSeparator())
                .append(calculator).append(System.lineSeparator())
                .append(yieldProperties).append(System.lineSeparator());
        return b.toString();
    }

    /**
     * Vykoná všetky akcie v množine actions zavolaním ich metódy
     * {@link Action#doAction()}.
     * @param actions 
     */
    private void doActions(Set<Action> actions) {
        for(Action a : actions){
            a.doAction();
        }
    }
    
    /**
     * Nastaví užívateľa pre túto season.
     * 
     * @param user aktuálny užívateľ
     */
    public void setUser(User user){
        this.user = user;
    }
    
    /**
     * Vráti aktuálneho užívateľa v tejto season.
     * 
     * @return aktuálny užívateľ
     */
    public User getUser(){
        return user;
    }
}
