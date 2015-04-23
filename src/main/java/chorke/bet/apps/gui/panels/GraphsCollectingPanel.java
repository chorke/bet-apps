
package chorke.bet.apps.gui.panels;

import chorke.bet.apps.core.CoreUtils.BetPossibility;
import chorke.bet.apps.core.CoreUtils.Periode;
import chorke.bet.apps.core.calculators.Yield;
import chorke.bet.apps.core.graphs.Graph;
import chorke.bet.apps.gui.GuiUtils;
import chorke.bet.apps.gui.Season;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 * Vytvorí panel s grafmi všetkých možností a podľa všetkých rozsahov.
 * Grafy budú zahŕňať všetky možné obdobia.
 * 
 * @author Chorke
 */
public class GraphsCollectingPanel extends JPanel{
    
    /**
     * forma (perióda, idx stávkovej možnosti, idx rozsahu)
     */
    private Map<Periode, GraphPanel[][]> graphs;
    private JRadioButton[] periodeButtons;
    private JRadioButton[] scaleButtons;
    private JRadioButton[] betPossButtons;
    /**
     * Panel, kde je zobrazený aktuálny graf.
     */
    private JPanel visibleGraph;
    private Dimension graphDim = new Dimension(490, 150);
    private JPanel propertiesPanel;
    private GraphPanel emptyGraph;
    private Dimension graphScrollPaneDim = new Dimension(500, 160);
    
    private ResourceBundle bundle;
    
    private Season season;

    /**
     * Vytvorí nový panel, ktorý zobrazuje grafy.
     * 
     * @param season 
     */
    public GraphsCollectingPanel(Season season) {
        if(season == null){
            throw new IllegalArgumentException("Season cannot be null.");
        }
        this.season = season;
        bundle = season.getDefaultBundle();
        graphs = new HashMap<>();
        periodeButtons = new JRadioButton[Periode.values().length];
        int i = 0;
        for(Periode p : Periode.values()){
            periodeButtons[i++] = GuiUtils.getRadioButton(bundle.getString(p.toString()),
                    new SetRequireGraphAction());
        }
        betPossButtons = new JRadioButton[season.getCalculator().getBetPossibilities().length];
        i = 0;
        for(BetPossibility bp : season.getCalculator().getBetPossibilities()){
            betPossButtons[i++] = GuiUtils.getRadioButton(bundle.getString(bp.toString()),
                    new SetRequireGraphAction());
        }
        GuiUtils.mergeToOneButtonGroup(betPossButtons);
        GuiUtils.mergeToOneButtonGroup(periodeButtons);
        scaleButtons = new JRadioButton[0];
        visibleGraph = new JPanel();
        visibleGraph.setPreferredSize(graphScrollPaneDim);
        init();
    }
    
    /**
     * Inicializácia panelu. Vytvorí všetky grafy a vloží panel pre graf a panel
     * pre nastavenia.
     */
    private void init(){
        prepareScale();
        propertiesPanel = new JPanel();
        propertiesPanel.add(initPropPanel());
        GroupLayout gl = new GroupLayout(this);
        gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(propertiesPanel)
                .addComponent(visibleGraph));
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(propertiesPanel)
                .addComponent(visibleGraph));
        setLayout(gl);
        
        emptyGraph = new GraphPanel(new Graph());
        emptyGraph.setPreferredSize(graphDim);
        
        prepareGraphsAndSetRequired();
    }
    
    /**
     * Pripraví nové grafy a nastaví požadovaný graf podľa nastavení.
     */
    private void prepareGraphsAndSetRequired(){
        prepareGraphs();
        setRequiredGraph();
    }
    
    /**
     * Nastaví požadovaný graf. Ak nie je niektorá z možností zvolená (perióda,
     * rozsah, možnosť stývky), je zobrazený prázdny graf.
     */
    private void setRequiredGraph(){
        int per = GuiUtils.getSelectedIdx(periodeButtons);
        int scal = GuiUtils.getSelectedIdx(scaleButtons);
        int betPossIdx = GuiUtils.getSelectedIdx(betPossButtons);
        visibleGraph.removeAll();
        if(per == -1 || scal == -1 || betPossIdx == -1){
            visibleGraph.add(emptyGraph);
        } else {
            JScrollPane pane = new JScrollPane(graphs.get(Periode.values()[per])[betPossIdx][scal]);
            pane.setPreferredSize(graphScrollPaneDim);
            pane.setBorder(null);
            visibleGraph.add(pane);
        }
        visibleGraph.paintAll(visibleGraph.getGraphics());
    }
    
    /**
     * Inicializuje panel s nastaveniami. Nastaví možnosti pre periodu podľa
     * Periode, pre scales podľa scales v YieldProperties a betPoss podľa 
     * podporovaných stávok podľa calculator.
     * 
     * @return 
     */
    private JPanel initPropPanel(){
        JPanel propPanel = new JPanel();
        propPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
                bundle.getString("propertiesTitle")));
        JLabel periodeLabel = new JLabel(bundle.getString("periode"));
        JLabel scaleLabel = new JLabel(bundle.getString("scale"));
        JLabel betPossLabel = new JLabel(bundle.getString("betPoss"));
        JPanel periodePanel = getButtonsPanel(periodeButtons);
        JPanel scalePanel = getButtonsPanel(scaleButtons);
        JPanel betPossPanel = getButtonsPanel(betPossButtons);
        GroupLayout gl = new GroupLayout(propPanel);
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup()
                    .addComponent(periodeLabel)
                    .addComponent(scaleLabel)
                    .addComponent(betPossLabel))
                .addGroup(gl.createParallelGroup()
                    .addComponent(periodePanel)
                    .addComponent(scalePanel)
                    .addComponent(betPossPanel)));
        
        gl.setVerticalGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                    .addComponent(periodeLabel)
                    .addComponent(scaleLabel)
                    .addComponent(betPossLabel))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(periodePanel)
                    .addComponent(scalePanel)
                    .addComponent(betPossPanel)));
        gl.linkSize(SwingConstants.VERTICAL, periodeLabel, periodePanel);
        gl.linkSize(SwingConstants.VERTICAL, scaleLabel, scalePanel);
        gl.linkSize(SwingConstants.VERTICAL, betPossLabel, betPossPanel);
        propPanel.setLayout(gl);
        int w = Math.min(propPanel.getPreferredSize().width, 800);
        graphDim.width = Math.max(w - 10, 0);
        graphScrollPaneDim.width = w;
        return propPanel;
    }
    
    /**
     * Vytvorí panel, ktorý bude obsahovať v jednom riadku všetky tlačítka 
     * zo skupiny group.
     * 
     * @param buttons
     * @return 
     */
    private JPanel getButtonsPanel(JRadioButton[] buttons){
        JPanel p = new JPanel();
        GroupLayout gl = new GroupLayout(p);
        Group g = gl.createSequentialGroup();
        for(JRadioButton b : buttons){
            g = g.addComponent(b);
        }
        gl.setHorizontalGroup(g);
        g = gl.createParallelGroup();
        for(JRadioButton b : buttons){
            g = g.addComponent(b);
        }
        gl.setVerticalGroup(g);
        p.setLayout(gl);
        return p;
    }
    
    /**
     * Pripraví rozsahy podľa YieldProperties. Zároveň nastavý scales.
     */
    private void prepareScale(){
        if(season.getYieldProperties() == null){
            return;
        }
        List<BigDecimal> scList = season.getYieldProperties().getScale();
        if(scList == null){
            scList = new LinkedList<>();
        }
        scaleButtons = new JRadioButton[scList.size() + 1];
        String last = BigDecimal.ONE.toPlainString();
        int i = 0;
        for(BigDecimal bd : scList){
            String next = bd.toPlainString();
            scaleButtons[i++] = GuiUtils.getRadioButton(last + "-" + next, new SetRequireGraphAction());
            last = next;
        }
        scaleButtons[i] = GuiUtils.getRadioButton(last + " +", new SetRequireGraphAction());
        GuiUtils.mergeToOneButtonGroup(scaleButtons);
    }
    
    /**
     * Pripraví všetky grafy podľa obdobia, aj podľa stávok a rozsahov.
     */
    private void prepareGraphs(){
        for(Periode p : Periode.values()){
            graphs.put(p, prepareGraphsPanel(p));
        }
    }
    
    /**
     * Pripraví grafy podľa stávok a podľa rozsahov pre zvolené obdobie.
     * 
     * @param periode
     * @return 
     */
    @SuppressWarnings("unchecked")
    private GraphPanel[][] prepareGraphsPanel(Periode periode){
        if(season.getCalculator() == null
                || season.getMatches() == null
                || season.getYieldProperties() == null){
            return new GraphPanel[0][0];
        }
        Map<Calendar, Yield> yields = season.getCalculator().getPeriodicYield(
                season.getMatches(), season.getYieldProperties(), periode);
        BetPossibility[] poss = season.getCalculator().getBetPossibilities();
        
        List<BigDecimal> scales = season.getYieldProperties().getScale();
        GraphPanel[][] graphsForPeriode = new GraphPanel[poss.length][scales.size() + 1];
        for(int i = 0; i < poss.length; i++){
            for(int j = 0; j <= scales.size(); j++){
                graphsForPeriode[i][j] = new GraphPanel(season.getGraphBuilder()
                        .getGraph(yields, poss[i], j));
                graphsForPeriode[i][j].setPreferredSize(graphDim);
            }
        }
        return graphsForPeriode;
    }
    
    /**
     * Akcia, ktorá po aktivácii nastaví požadovaný graf (aktuálne
     * nastavné parametre).
     */
    private class SetRequireGraphAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            setRequiredGraph();
        }
    }
}