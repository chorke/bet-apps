
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.bets.Bet1x2;
import chorke.bet.apps.core.bets.BetAsianHandicap;
import chorke.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.bet.apps.core.bets.BetDoubleChance;
import chorke.bet.apps.core.bets.BetDrawNoBet;
import chorke.bet.apps.core.bets.BetOverUnder;
import chorke.bet.apps.core.calculators.Yield;
import chorke.bet.apps.core.calculators.Yield1x2Calculator;
import chorke.bet.apps.core.calculators.YieldCalculator;
import chorke.bet.apps.core.calculators.YieldProperties;
import chorke.bet.apps.core.graphs.GraphBuilder;
import chorke.bet.apps.core.graphs.GraphBuilderYield1x2;
import chorke.bet.apps.core.match.Match;
import chorke.bet.apps.gui.Action;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Season;
import chorke.bet.apps.io.BetIOException;
import chorke.bet.apps.io.LoadProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import org.chorke.gui.utils.panels.NotesPanel;


/**
 * Hlavný panel aplikácie.
 * 
 * @author Chorke
 */
public class MainPanel extends JPanel{

    private static final Yield1x2Calculator YC_1x2 = new Yield1x2Calculator();
    private static final GraphBuilderYield1x2 GB_1x2 = new GraphBuilderYield1x2();
    
    private DownloadPanel dwnPanel;
    private LoadingPanel loadPanel;
    private NotesPanel notes;
    private DeletePanel deletePanel;
    private Season season;
    
    private JButton dwnButton;
    private JButton loadButton;
    private JButton getStatsButton;
    private JButton getGraphsButton;
    private JButton deleteButton;
    
    private JRadioButton[] betTypesButtons;
    private JRadioButton[] betCompaniesButtons;
    
    private JPanel statsResultsPanel;
    private JPanel scalesAndBetTypesPanel;
    private JPanel betCompaniesPanel;
    
    
    private JTextField scales;
    
    private ResourceBundle bundle;
    
    /**
     * Vytvorí nový panel.
     * 
     * @param season 
     * @throws IllegalArgumentException ak je season = null
     */
    public MainPanel(Season season) {
        if(season == null){
            throw new IllegalArgumentException("Season cannot be null.");
        }
        this.season = season;
        this.season.addActionAfterLoadPropertiesSet(new UpdateBetCompanies());
        bundle = season.getDefaultBundle();
        init();
    }
    
    /**
     * Inicializuje panel.
     */
    private void init(){
        initScalesAndBetsTypePanel();
        statsResultsPanel = new JPanel();
        betCompaniesPanel = new JPanel();
        betCompaniesButtons = new JRadioButton[0];
        initButtons();
        
        JScrollPane paneResults = new JScrollPane(statsResultsPanel);
        JScrollPane paneBetCompanies = new JScrollPane(betCompaniesPanel);
        notes = new NotesPanel(season.getUser().getNotesFile());
        JScrollPane paneNotes = new JScrollPane(notes);
        
        JLabel betCompLab = new JLabel(bundle.getString("betComp"));
        JLabel statsLab = new JLabel(bundle.getString("stats"));
        
        GroupLayout gl = new GroupLayout(this);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                    .addGroup(gl.createParallelGroup()
                        .addComponent(dwnButton)
                        .addComponent(loadButton)
                        .addComponent(getStatsButton)
                        .addComponent(getGraphsButton)
                        .addComponent(deleteButton))
                    .addGroup(gl.createParallelGroup()
                        .addComponent(scalesAndBetTypesPanel, 400, 400, 400)
                        .addComponent(betCompLab)
                        .addComponent(paneBetCompanies)
                        .addComponent(statsLab)
                        .addComponent(paneResults))
                    .addComponent(paneNotes, 200, 200, 200));
        gl.setVerticalGroup(gl.createParallelGroup()
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(dwnButton)
                        .addComponent(loadButton)
                        .addComponent(getStatsButton)
                        .addComponent(getGraphsButton)
                        .addComponent(deleteButton))
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(scalesAndBetTypesPanel, 150, 150, 150)
                        .addComponent(betCompLab)
                        .addComponent(paneBetCompanies, 80, 80, 80)
                        .addComponent(statsLab)
                        .addComponent(paneResults, 150, 150, 150))
                    .addComponent(paneNotes, 435, 435, 435));
        gl.linkSize(dwnButton, loadButton, getGraphsButton, getStatsButton, deleteButton);
        gl.linkSize(betCompLab, statsLab);
        gl.linkSize(SwingConstants.HORIZONTAL, paneBetCompanies, paneResults);
        gl.linkSize(SwingConstants.HORIZONTAL, scalesAndBetTypesPanel, paneBetCompanies);
        setLayout(gl);
    }

    /**
     * Inicializuje tlačítka - bočné pre sťahovanie, načítavanie a počítanie
     * štatistík.
     */
    private void initButtons(){
        dwnButton = new JButton(bundle.getString("download"));
        loadButton = new JButton(bundle.getString("load"));
        getStatsButton = new JButton(bundle.getString("getStats"));
        getGraphsButton = new JButton(bundle.getString("getGraphs"));
        deleteButton = new JButton(bundle.getString("delete"));
        dwnButton.addActionListener(new ShowDownloadWindow());
        loadButton.addActionListener(new ShowLoadWindow());
        getStatsButton.addActionListener(new GetStatsActionListener());
        getGraphsButton.addActionListener(new ShowGraphsWindow());
        deleteButton.addActionListener(new ShowDeleteWindow());
    }
    
    /**
     * Ukončí prácu s týmto objektom.
     */
    public void destroy(){
        notes.saveNote();
    }
    
    /**
     * Vloží tlačítka stávkových spoločností. Vyberá z tých, ktoré boli naposledy
     * zahrnuté pri loadingu.
     */
    private void putBetCompaniestButtons(){
        LoadProperties prop = season.getLoadProperties();
        if(prop == null){
            return;
        }
        Set<String> com = prop.getBetCompanies();
        if(com == null || com.isEmpty()){
            betCompaniesButtons = new JRadioButton[0];
            return;
        }
        betCompaniesButtons = new JRadioButton[com.size()];
        betCompaniesPanel.removeAll();
        GridLayout gl = new GridLayout(GuiUtils.getNumOfRows(2, betCompaniesButtons.length), 2);
        betCompaniesPanel.setLayout(gl);
        int i = 0;
        for(String s : com){
            betCompaniesButtons[i] = new JRadioButton(s);
            betCompaniesPanel.add(betCompaniesButtons[i++]);
        }
        GuiUtils.mergeToOneButtonGroup(betCompaniesButtons);
        betCompaniesPanel.paintAll(betCompaniesPanel.getGraphics());
    }
    
    /**
     * Vytvorí panel s výsledkymi spočítaných štatistík. Panel obsahuje iba
     * celkový zisk.
     */
    private void putYieldToStatsResulstPanel(){
        GuiUtils.showWaitingDialog(bundle.getString("gettingYields"));
        prepareSeasonForGettingStats();
        Yield yield = getYield();
        if(yield != null){
            List<BigDecimal> scls = season.getYieldProperties().getScale();
            statsResultsPanel.removeAll();
            GroupLayout gl = new GroupLayout(statsResultsPanel);
            Group horizontal = gl.createSequentialGroup();
            Group vertical = gl.createParallelGroup();
            BigDecimal last = BigDecimal.ONE;
            int idx = 0;
            for(BigDecimal bd : scls){
                add(horizontal, vertical, 
                        last.toPlainString() + " - " + bd.toPlainString(),
                        yield, idx++);
                last = bd;
            }
            add(horizontal, vertical, 
                        last.toPlainString() + " +", yield, idx++);
            gl.setAutoCreateContainerGaps(true);
            gl.setAutoCreateGaps(true);
            gl.setHorizontalGroup(horizontal);
            gl.setVerticalGroup(vertical);
            statsResultsPanel.setLayout(gl);
        }
        GuiUtils.hideWaitingDialog();
    }
    
    /**
     * Nastaví všetky potrebné parametre pre season pre počítanie ziskov.
     * Teda nastaví yield properties podľa aktuálne zvolených rozsahov v 
     * {@link #scales} a stávkovej spoločnosti z {@link #betCompaniesButtons}
     * a nastaví strávny {@link YieldCalculator} a {@link GraphBuilder} podľa
     * zvoleného typu stávky.
     */
    private void prepareSeasonForGettingStats(){
        setYieldProperties();
        setCalculatorAndGraphBuilder();
    }
    
    /**
     * Nastaví správny YieldCalculator a k nemu zodpovedajúci GraphBuilder
     * podľa aktuálne zvoleného typu stávok.
     */
    private void setCalculatorAndGraphBuilder(){
        for(JRadioButton rb : betTypesButtons){
            if(rb.isSelected()){
                if(rb.getText().equals(bundle.getString(Bet1x2.class.getSimpleName()))){
                    season.setCalculator(YC_1x2);
                    season.setGraphBuilder(GB_1x2);
                } else if(rb.getText().equals(bundle.getString(BetAsianHandicap.class.getSimpleName()))){
                } else if(rb.getText().equals(bundle.getString(BetBothTeamsToScore.class.getSimpleName()))){
                } else if(rb.getText().equals(bundle.getString(BetDoubleChance.class.getSimpleName()))){
                } else if(rb.getText().equals(bundle.getString(BetDrawNoBet.class.getSimpleName()))){
                } else if(rb.getText().equals(bundle.getString(BetOverUnder.class.getSimpleName()))){
                } else {
                    
                }
                return;
            }
        }
    }
    
    /**
     * Nastaví YieldProperties do {@link #season}. Berie aktuálne
     * zvolenú škálu z {@link #scales} a zvolenú stávkovú spoločnosť.
     */
    private void setYieldProperties(){
        YieldProperties prop = new YieldProperties();
        String text = scales.getText();
        String[] splitted = text.split("[, ]+");
        if(!text.isEmpty() && splitted.length > 0){
            MathContext mc = new MathContext(3);
            for(String s : splitted){
                try{
                    prop.addScale(new BigDecimal(s).round(mc));
                } catch (NumberFormatException | ArithmeticException ex){
                    System.out.println(ex);
                }
            }
        }
        for(JRadioButton rb : betCompaniesButtons){
            if(rb.isSelected()){
                prop.setBetCompany(rb.getText());
            }
        }
        season.setYieldProperties(prop);
    }
    
    /**
     * Vráti celkový zisk z aktuálnych parametrov v {@link #season}.
     * @return null ak nie sú nastavené parametre
     */
    @SuppressWarnings("unchecked")
    private Yield getYield(){
        YieldCalculator calc = season.getCalculator();
        Collection<Match> matches = season.getMatches();
        YieldProperties prop = season.getYieldProperties();
        if(calc != null && matches != null && prop != null){
            return calc.getOverallYield(matches, prop);
        }
        return null;
    }
    
    /**
     * Vytvorí nový panel, ktorý bude obsahovať všetky výsledky stávkových
     * možností z yield pre zvolený index škály idx. Pridá panel ako do 
     * vertikálnej skupiny vertical, tak do horizontálnej horizontal.
     * 
     * @param horizontal horizontálna skupina
     * @param vertical vertikálna skupina
     * @param sclLabel label pre index idx, ktorý bude použitý ako identifikátor
     *          nového panelu (bude v TitledBorder)
     * @param yield zisk
     * @param idx index na škále
     */
    private void add(Group horizontal, Group vertical,
                String sclLabel, Yield yield, int idx){
        JPanel pan = new JPanel();
        JLabel scLab = new JLabel(sclLabel);
        GroupLayout gl = new GroupLayout(pan);
        Group gSeq = gl.createSequentialGroup().addComponent(scLab);
        Group gPar = gl.createParallelGroup().addComponent(scLab);
        for(BetPossibility bp : yield.getSupportedBetPossibilities()){
            BigDecimal y = yield.getYieldForScaleIndex(bp, idx , true);
            JLabel valLab = new JLabel(getBetPossString(bp) + y.toPlainString() + " %");
            valLab.setForeground(y.signum() > 0 ? Color.GREEN : Color.RED);
            gSeq.addComponent(valLab);
            gPar.addComponent(valLab);
        }
        gl.setHorizontalGroup(gPar);
        gl.setVerticalGroup(gSeq);
        pan.setLayout(gl);
        pan.setBorder(new LineBorder(Color.BLACK));
        horizontal.addComponent(pan);
        vertical.addComponent(pan);
    }
    
    private String getBetPossString(BetPossibility bp){
        switch(bp){
            case Favorit:
                return "F: ";
            case Guest:
                return "2: ";
            case Home:
                return "1: ";
            case Loser:
                return "O: ";
            case Tie:
                return "x: ";
        }
        return "";
    }
    
    /**
     * Pripraví panel so škálou a typom stávky.
     */
    private void initScalesAndBetsTypePanel(){
        betTypesButtons = new JRadioButton[GuiUtils.SUPPORTED_BET_TYPES.length];
        int i = 0;
        int initIdx = GuiUtils.getIndexOfClassInSupportedClasses(Bet1x2.class);
        for(Class c : GuiUtils.SUPPORTED_BET_TYPES){
            betTypesButtons[i] = new JRadioButton(bundle.getString(c.getSimpleName()));
            if(i == initIdx){
                betTypesButtons[i].setSelected(true);
            }
            i++;
        }
        GuiUtils.mergeToOneButtonGroup(betTypesButtons);
        
        scales = new JTextField();
        scales.addKeyListener(new DigitOnlyKeyListener());
        JPanel betTypes = new JPanel(new GridLayout(
                GuiUtils.getNumOfRows(2, betTypesButtons.length), 2));
        for(JRadioButton b : betTypesButtons){
            betTypes.add(b);
        }
        betTypes.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        scalesAndBetTypesPanel = new JPanel();
        JLabel scLab = new JLabel(bundle.getString("scale"));
        JLabel betTypeLab = new JLabel(bundle.getString("betType"));
        GroupLayout gl = new GroupLayout(scalesAndBetTypesPanel);
        
        gl.setAutoCreateGaps(true);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(scLab)
                .addComponent(scales, 20, 20, 20)
                .addComponent(betTypeLab)
                .addComponent(betTypes));
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(scLab)
                .addComponent(scales)
                .addComponent(betTypeLab)
                .addComponent(betTypes));
        gl.linkSize(scLab, betTypeLab);
        scalesAndBetTypesPanel.setLayout(gl);
    }
    
    /**
     * Akcia pre zobrazenie download okna.
     */
    private class ShowDownloadWindow implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            loadPanel = null;
            if(dwnPanel == null){
                dwnPanel = new DownloadPanel(season);
            }
            GuiUtils.getDefaultFrame(bundle.getString("download"), JFrame.DISPOSE_ON_CLOSE,
                    false, null, dwnPanel).setVisible(true);
        }
    }
    
    /**
     * Akcia pre zobrazenie load okna. Akcia je spustená v novom vlákne, aby
     * event dispatch thread nebolo blokované pri zobrazení čakacieho
     * dialógu (aplikácia by zamrzla).
     */
    private class ShowLoadWindow implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new ShowLoadWindowThread()).start();
        }
    }
    
    /**
     * Vlákno, ktoré spustí load okno. Nebude tak blokobané EDT.
     */
    private class ShowLoadWindowThread implements Runnable{
        
        @Override
        public void run(){
            if(loadPanel == null){
                try{
                    GuiUtils.showWaitingDialog(bundle.getString("initLoad"));
                    loadPanel = new LoadingPanel(season);
                } catch (BetIOException ex){
                    loadPanel = null;
                    JOptionPane.showMessageDialog(null, bundle.getString("errLoadWindCreation")
                            + System.lineSeparator() + ex.getCause());
                } finally {
                    GuiUtils.hideWaitingDialog();
                }
            }
            if(loadPanel != null){
                GuiUtils.getDefaultFrame(bundle.getString("load"), JFrame.DISPOSE_ON_CLOSE,
                        false, null, loadPanel).setVisible(true);
            }
        }
        
    }
    
    /**
     * Akcia pre zobrazenie okna s grafmi. Akcia je spustená v novom vlákne, aby
     * event dispatch thread nebolo blokované pri zobrazení čakacieho
     * dialógu (aplikácia by zamrzla).
     */
    private class ShowGraphsWindow implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new ShowGraphsWindowThread()).start();
        }
        
    }
    
    /**
     * Vlákno, ktoré vytvorí nové okno s grafmi. Nebude tak blokované EDT.
     */
    private class ShowGraphsWindowThread implements Runnable {

        @Override
        public void run() {
            GuiUtils.showWaitingDialog(bundle.getString("initGraphs"));
            prepareSeasonForGettingStats();
            JScrollPane pane = new JScrollPane(new GraphsCollectingPanel(season));
            pane.setPreferredSize(new Dimension(450, 300));
            GuiUtils.getDefaultFrame(bundle.getString("graphs"), JFrame.DISPOSE_ON_CLOSE,
                    true, null, pane).setVisible(true);
            GuiUtils.hideWaitingDialog();
        }
        
    }
    
    private class ShowDeleteWindow implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new ShowDeleteWindowThread()).start();
        }
        
    }
    
    private class ShowDeleteWindowThread implements Runnable{

        @Override
        public void run() {
            try{
                GuiUtils.showWaitingDialog(bundle.getString("initDelete"));
                deletePanel = new DeletePanel(season);
            } catch (BetIOException ex){
                deletePanel = null;
                JOptionPane.showMessageDialog(null, bundle.getString("errDeleteWindCreation")
                        + System.lineSeparator() + ex.getCause());
            } finally {
                GuiUtils.hideWaitingDialog();
            }
            if(deletePanel != null){
                GuiUtils.getDefaultFrame(bundle.getString("delete"), JFrame.DISPOSE_ON_CLOSE,
                        false, null, deletePanel).setVisible(true);
            }
        }
        
    }
    
    /**
     * Akcia pre vypočítanie čtatistík. Akcia je spustená v novom vlákne, aby
     * event dispatch thread nebolo blokované pri zobrazení čakacieho
     * dialógu (aplikácia by zamrzla).
     */
    private class GetStatsActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(new GetStatsThread()).start();
        }
    }
    
    /**
     * Vlákno pre počítanie štatistík. Nebude tak blokované EDT.
     */
    private class GetStatsThread implements Runnable{
        
        @Override
        public void run(){
            putYieldToStatsResulstPanel();
        }
    }
    
    /**
     * Akcia, ktorá aktualizuje zoznam stávkových spoločností.
     */
    private class UpdateBetCompanies extends Action{

        @Override
        public void doAction() {
            putBetCompaniestButtons();
        }
        
    }
    
    /**
     * Akcia, ktorá aktualizuje informácie o aktuálne načítných zápasoch.
     */
    private class UpdateInfo extends Action{

        @Override
        public void doAction() {
            //TODO
        }
        
    }
    
    /**
     * KeyListener, ktorý obmedzuje vkladanie textu iba na číslice, znaky
     * čiarku (','), bodku ('.') medzeru (' ').
     */
    private class DigitOnlyKeyListener extends KeyAdapter {
        
        @Override
        public void keyTyped(KeyEvent e) {
            checkEventKeyChar(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            checkEventKeyChar(e);
        }
        
        /**
         * Skontroluje, či má byť KeyEvent e spracovaný alebo nie. Ak stlačená
         * klávesa nie je z podporovaných, potom e už nebude ďalej spracovávaný.
         * @param e 
         */
        private void checkEventKeyChar(KeyEvent e){
            if(!isSupportedCharacter(e.getKeyChar())){ 
                e.consume();
                Toolkit.getDefaultToolkit().beep();
            }
        }
        
        /**
         * Rozhodne, či je znak c podporovaný alebo nie. Podporvované sú
         * bodka, čiarka, medzera, číslo a špeciálne kombinácie pre 
         * výber, označenie, mazanie a kopírovanie textu (pre Windows).
         * @param c
         * @return 
         */
        private boolean isSupportedCharacter(char c){
            /*
             * 8 - backspace
             * 37 - left arrow
             * 38 - right arrow
             * -1 - shift/ctrl/alt mask
             * 24 - ctrl+x
             * 3 - ctrl+c
             * 22 - ctrl+v
             * 1 - ctrl+a
             * 127 - ctrl+backspace
             */
            return c == 8 || c == 37 || c == 39 || (byte)c == -1
                    || c == ',' || c == '.' || c == ' '
                    || c == 24 || c == 3 || c == 22 || c == 1 || c == 127
                    || Character.isDigit(c);
        }
    }
}
