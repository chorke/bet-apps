
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.bets.Bet;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Session;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Chorke
 */
public class BetCompaniesListPanel extends JPanel{
    /**
     * Podporované typy stávok.
     */
    private Class<? extends Bet>[] supportedBetsType = GuiUtils.SUPPORTED_BET_TYPES;
    /**
     * Dostupné stávkové spoločnosti v DB. Ukladaný formát je (typ stávky,
     * zoznam spoločností).
     */
    private Map<Class<? extends Bet>, Collection<String>> betCompanies;
    
    /**
     * Skupina tlačítok, ktoré slúžia pre zvolenie typu stávky. 
     */
    private JRadioButton[] betTypeButtons;
    /**
     * ResourceBundle pre lokalizáciu a i18n.
     */
    private ResourceBundle bundle;
    /**
     * Akutálne zvolený požedovaný typ stávky.
     */
    private Class<? extends Bet> betClassRequired;
    /**
     * CheckBox tlačítka pre dostupné stávkové spoločnosti. 
     */
    private JCheckBox[] betCompaniesButtons;
    /**
     * Panel, ktorý obsahuje zoznam aktuálne zvolených stávkových spoločností.
     */
    private JPanel betCompaniesList;
    
    
    /**
     * Vytvorí nový panel pre stávkové spoločnosti.
     * 
     * @param betCompanies mapa typov stávok a k nim priradených stávkových spoločností
     * @param initialChoosenBetClass typ stávky, ktorý má byť iniciálne vybraný
     * @param initialChoosenBetCompanies stávkové spoločnosti, ktoré majú byť inicálne vybraní
     * @param session aktuálne sedenie (pre {@link ResourceBundle} pre lokalizované názvy)
     */
    public BetCompaniesListPanel(Map<Class<? extends Bet>, Collection<String>> betCompanies,
            Class<? extends Bet> initialChoosenBetClass, Collection<String> initialChoosenBetCompanies,
            Session session){
        if(betCompanies == null){
            throw new IllegalArgumentException("Bet companies are null.");
        }
        this.betCompanies = betCompanies;
        bundle = session.getDefaultBundle();
        init(initialChoosenBetClass, initialChoosenBetCompanies);
    }
    
    /**
     * Inicializuje panel pre stávkové spoločnosti.
     */
    private void init(Class<? extends Bet> initialChoosenBetClass,
            Collection<String> initialChoosenBetCompanies){
        setBorder(new TitledBorder(new LineBorder(Color.BLACK),
                bundle.getString("betComp")));
        
        int initIdx = 0;
        if(initialChoosenBetClass != null){
            initIdx = GuiUtils.getIndexOfClassInSupportedClasses(
                    initialChoosenBetClass);
        }
        boolean betClassSet = initIdx >= 0;
        initIdx = initIdx >= 0 ? initIdx : 0;
        betClassRequired = supportedBetsType[initIdx];
        
        betTypeButtons = new JRadioButton[supportedBetsType.length];
        int i = 0;
        for(Class<? extends Bet> c : supportedBetsType){
            betTypeButtons[i] = GuiUtils.getRadioButton(bundle.getString(c.getSimpleName()),
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
        if(betClassSet && initialChoosenBetCompanies != null){
            for(JCheckBox cb : betCompaniesButtons){
                if(initialChoosenBetCompanies.contains(cb.getText())){
                    cb.setSelected(true);
                }
            }
        }
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateGaps(true);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(betPossPanel)
                .addComponent(betCompaniesList));
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(betPossPanel)
                .addComponent(betCompaniesList));
        
        setLayout(gl);
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
        if(comp == null){
            betCompaniesButtons = new JCheckBox[0];
        } else {
            betCompaniesButtons = new JCheckBox[comp.size()];
            int i = 0;
            for(String s : comp){
                betCompaniesButtons[i] = new JCheckBox(s);
                betCompaniesButtons[i++].setBackground(Color.WHITE);
            }
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
     * Vráti aktuálne zvolený typ stávky.
     * 
     * @return aktuálny typ stávky
     */
    public Class<? extends Bet> getActualChosenBetType(){
        return betClassRequired;
    }
    
    /**
     * Vráti názvy všetkých stávkových spoločností, ktoré sú aktuálne vybrané.
     * 
     * @return názvy zvolených stávkových spoločností
     */
    public Set<String> getActualChosenBetCompanies(){
        Set<String> bc = new HashSet<>();
        for(JCheckBox cb : betCompaniesButtons){
            if(cb.isSelected()){
                bc.add(cb.getText());
            }
        }
        return bc;
    }
    
    /**
     * Akcia, ktorá nastaví stávkové spoločnosti. Spoločnosti sú nastavené podľa
     * argumentu {@link #cls}, ktorý je predávaný pri konštruktore.
     */
    private class BetTypeSet implements ActionListener {

        private Class<? extends Bet> cls;

        public BetTypeSet(Class<? extends Bet> cls) {
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
}
