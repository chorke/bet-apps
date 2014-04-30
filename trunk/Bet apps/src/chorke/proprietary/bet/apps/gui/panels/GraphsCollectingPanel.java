
package chorke.proprietary.bet.apps.gui.panels;

import chorke.proprietary.bet.apps.StaticConstants;
import chorke.proprietary.bet.apps.StaticConstants.BetPossibility;
import chorke.proprietary.bet.apps.StaticConstants.Periode;
import chorke.proprietary.bet.apps.core.calculators.Yield;
import chorke.proprietary.bet.apps.core.calculators.YieldCalculator;
import chorke.proprietary.bet.apps.core.calculators.YieldProperties;
import chorke.proprietary.bet.apps.core.graphs.Graph;
import chorke.proprietary.bet.apps.core.graphs.GraphBuilder;
import chorke.proprietary.bet.apps.core.match.Match;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
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
 * Vytvorí panel so grafmi všetkých možností a podľa všetkých rozsahov.
 * Grafy budú zahŕňať všetky možné obdobia.
 * 
 * @author Chorke
 */
public class GraphsCollectingPanel extends JPanel{
    
    private YieldCalculator calculator;
    private GraphBuilder graphBuilder;
    private List<Match> matches;
    private YieldProperties properties;
    /**
     * forma (perióda, idx stávkovej možnosti, idx rozsahu)
     */
    private Map<Periode, GraphPanel[][]> graphs;
    private String[] scale;
    private String[] periode;
    private String[] betPoss;
    private ButtonGroup periodeGroup;
    private ButtonGroup scaleGroup;
    private ButtonGroup betPossGroup;
    private JPanel visibleGraph;
    private Dimension graphDim = new Dimension(490, 130);
    private JPanel propertiesPanel;
    private GraphPanel emptyGraph;
    private Dimension graphScrollPaneDim = new Dimension(500, 150);
    
    private ResourceBundle bundle = StaticConstants.BUNDLE;

    /**
     * Vytvorí nový panel, ktorý zobrazuje grafy.
     * 
     * @param calculator podľa ktorého sa budú počítať zisky
     * @param graphBuilder ktorý je schopný vytvoriť graf pre zisky získané 
     * od calculator
     */
    public GraphsCollectingPanel(YieldCalculator calculator, GraphBuilder graphBuilder) {
        this.calculator = calculator;
        this.graphBuilder = graphBuilder;
        matches = new ArrayList<>();
        properties = new YieldProperties();
        graphs = new HashMap<>();
        periodeGroup = new ButtonGroup();
        periode = new String[Periode.values().length];
        int i = 0;
        for(Periode p : Periode.values()){
            periode[i] = bundle.getString(p.toString());
            periodeGroup.add(getRadioButton(periode[i++]));
        }
        betPoss = new String[calculator.getBetPossibilities().length];
        betPossGroup = new ButtonGroup();
        i = 0;
        for(BetPossibility bp : calculator.getBetPossibilities()){
            betPoss[i] = bundle.getString(bp.toString());
            betPossGroup.add(getRadioButton(betPoss[i++]));
        }
        scaleGroup = new ButtonGroup();
        visibleGraph = new JPanel();
        visibleGraph.setPreferredSize(graphScrollPaneDim);
        init();
    }
    
    /**
     * Inicializácia panelu. Vytvorí všetky grafy a vloží panel pre graf a panel
     * pre nastavenia.
     */
    private void init(){
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
        int per = getSelectedIdx(periodeGroup, periode);
        int scal = getSelectedIdx(scaleGroup, scale);
        int betPossIdx = getSelectedIdx(betPossGroup, betPoss);
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
     * Vráti index, ktorý je zvolený v rámci group. Porovnáva actionComands
     * v skupine group a v commands. Ak nie je zvolené žiadne tlačítko
     * alebo ak sa zvolený action command nenachádza medzi commands, 
     * tak metóda vráti -1.
     * 
     * @param group
     * @param commands pole acctionCommands, kuz ktorému budú príslušne vracané
     * indexy.
     * @return 
     */
    private int getSelectedIdx(ButtonGroup group, String[] commands){
        ButtonModel selection = group.getSelection();
        if(selection == null){
            return -1;
        }
        String selStr = selection.getActionCommand();
        int i = 0;
        for(String s : commands){
            if(s.equals(selStr)){
                return i;
            }
            i++;
        }
        return -1;
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
        JPanel periodePanel = getButtonsPanel(periodeGroup);
        JPanel scalePanel = getButtonsPanel(scaleGroup);
        JPanel betPossPanel = getButtonsPanel(betPossGroup);
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
    private JPanel getButtonsPanel(ButtonGroup buttons){
        JPanel p = new JPanel();
        GroupLayout gl = new GroupLayout(p);
        Group g = gl.createSequentialGroup();
        for(Enumeration<AbstractButton> b = buttons.getElements(); b.hasMoreElements();){
            g = g.addComponent(b.nextElement());
        }
        gl.setHorizontalGroup(g);
        g = gl.createParallelGroup();
        for(Enumeration<AbstractButton> b = buttons.getElements(); b.hasMoreElements();){
            g = g.addComponent(b.nextElement());
        }
        gl.setVerticalGroup(g);
        p.setLayout(gl);
        return p;
    }
    
    /**
     * Pripraví rozsahy podľa YieldProperties. Zároveň nastavý scales.
     */
    private void prepareScale(){
        scale = new String[properties.getScale().size() + 1];
        String last = BigDecimal.ONE.toPlainString();
        int i = 0;
        for(BigDecimal bd : properties.getScale()){
            String next = bd.toPlainString();
            scale[i++] = last + "-" + next;
            last = next;
        }
        scale[i] = last + " +";
        scaleGroup = new ButtonGroup();
        for(String s : scale){
            scaleGroup.add(getRadioButton(s));
        }
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
    private GraphPanel[][] prepareGraphsPanel(Periode periode){
        Map<Calendar, Yield> yields = calculator.getPeriodicYield(matches, properties, periode);
        BetPossibility[] poss = calculator.getBetPossibilities();
        
        List<BigDecimal> scales = properties.getScale();
        GraphPanel[][] graphsForPeriode = new GraphPanel[poss.length][properties.getScale().size() + 1];
        for(int i = 0; i < poss.length; i++){
            for(int j = 0; j <= scales.size(); j++){
                graphsForPeriode[i][j] = new GraphPanel(graphBuilder.getGraph(yields, poss[i], j));
                graphsForPeriode[i][j].setPreferredSize(graphDim);
            }
        }
        return graphsForPeriode;
    }
    
    /**
     * Nastaví zápasy, z ktorých sa budú počítať zisky. Pripravý nové grafy ku nim
     * a vymaže všetky zvolené nastavenia.
     * 
     * @param matches 
     */
    public void setMatches(Collection<Match> matches){
        this.matches.clear();
        this.matches.addAll(matches);
        periodeGroup.clearSelection();
        scaleGroup.clearSelection();
        betPossGroup.clearSelection();
        prepareGraphsAndSetRequired();
    }

    /**
     * Nastaví YieldProperties. Podľa nich sa budú počítať zisky. Pripraví
     * nové grafy. Vhodne upraví panel s nastaveniami.
     * 
     * @param properties 
     */
    public void setProperties(YieldProperties properties) {
        this.properties = properties;
        prepareScale();
        propertiesPanel.removeAll();
        propertiesPanel.add(initPropPanel());
        propertiesPanel.paintAll(propertiesPanel.getGraphics());
        prepareGraphsAndSetRequired();
    }
    
    /**
     * Vytvorí nové JRadioButton s textom a actionCommands text. Nastaví mu 
     * ActionListener tak, že po stlačení sa znovu vykreslí požadovaný graf.
     * 
     * @param text
     * @return 
     */
    private JRadioButton getRadioButton(String text){
        JRadioButton butt = new JRadioButton(text);
        butt.setActionCommand(text);
        butt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setRequiredGraph();
            }
        });
        return butt;
    }
}