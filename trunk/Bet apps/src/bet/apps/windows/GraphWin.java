package bet.apps.windows;

import bet.apps.BetApps;
import bet.apps.core.GraphPainterImpl;
import bet.apps.core.PaintableGraph;
import bet.apps.statistics.PeriodicStatistics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class GraphWin extends JFrame{

    private static final int WIDTH_WIN =  900;
    private static final int HEIGHT_WIN =  650;
    private static final int HEIGHT_BUTTONS = 100;
    private static final int HEIGHT_GRAPH = HEIGHT_WIN - HEIGHT_BUTTONS;
    
    public static final Color[] graphsColors = {Color.BLACK, Color.RED, Color.BLUE,
                                    Color.YELLOW, Color.GREEN.darker(), Color.PINK};
    private JCheckBox[] boxes;
    private JTabbedPane graphTabbedPanel;
            
    private int numOfFields = MainWin.NUM_OF_FIELDS;
    private int numOfStats = MainWin.NUM_OF_STATS - 2;
    
    private PeriodicStatistics statisticsByDay;
    private PeriodicStatistics statisticsByWeek;
    private PeriodicStatistics statisticsByMonth;
    private PeriodicStatistics statisticsByYear;
    private double[] bets;
    
    private List<List<PaintableGraph>> dayGraphs;      //<Fileds<Stats>>
    private List<List<PaintableGraph>> weekGraphs;
    private List<List<PaintableGraph>> monthGraphs;
    private List<List<PaintableGraph>> yearGraphs;
    private List<List<PaintableGraph>> actualGraphs;
    
    private GraphPainterImpl graphPainter;
                    
    public GraphWin(PeriodicStatistics statisticsByDay,
            PeriodicStatistics statisticsByWeek,
            PeriodicStatistics statisticsByMonth,
            PeriodicStatistics statisticsByYear,
            double[] bets){
        
        this.statisticsByDay = statisticsByDay;
        this.statisticsByWeek = statisticsByWeek;
        this.statisticsByMonth = statisticsByMonth;
        this.statisticsByYear = statisticsByYear;
        this.bets = bets;
        this.graphPainter = new GraphPainterImpl(new Dimension(WIDTH_WIN - 5, HEIGHT_GRAPH - 28));
        this.dayGraphs = this.statisticsByDay.getPaintableGraphs(this.bets, numOfStats);
        this.weekGraphs = this.statisticsByWeek.getPaintableGraphs(this.bets, numOfStats);
        this.monthGraphs = this.statisticsByMonth.getPaintableGraphs(this.bets, numOfStats);
        this.yearGraphs = this.statisticsByYear.getPaintableGraphs(this.bets, numOfStats);
        this.actualGraphs = weekGraphs;
        init();
        repaintTabs();
    }
    
    private void init(){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(WIDTH_WIN, HEIGHT_WIN));
        add(mainPanel);
        
        JPanel optionPanel = new JPanel();
        optionPanel.setPreferredSize(new Dimension(WIDTH_WIN, HEIGHT_BUTTONS));
        optionPanel.setMaximumSize(new Dimension(WIDTH_WIN, HEIGHT_BUTTONS));
        optionPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY),
                BetApps.LABLES.getString("graphMode")));
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        
        JButton dayGraphsButton = new JButton(BetApps.LABLES.getString("day"));
        dayGraphsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actualGraphs = dayGraphs;
                repaintTabs();
            }
        });
        
        JButton weekGraphsButton = new JButton(BetApps.LABLES.getString("week"));
        weekGraphsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actualGraphs = weekGraphs;
                repaintTabs();
            }
        });
        
        JButton monthGraphsButton = new JButton(BetApps.LABLES.getString("month"));
        monthGraphsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actualGraphs = monthGraphs;
                repaintTabs();
            }
        });
        
        JButton yearGraphsButton = new JButton(BetApps.LABLES.getString("year"));
        yearGraphsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actualGraphs = yearGraphs;
                repaintTabs();
            }
        });
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        
        MainWin.addToPanelWithGlue(buttonsPanel,
                dayGraphsButton, weekGraphsButton, monthGraphsButton,yearGraphsButton);
        
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.X_AXIS));
        createCheckBoxes();
        
        MainWin.addToPanelWithGlue(checkBoxPanel, boxes);
        MainWin.addToPanelWithGlue(optionPanel, buttonsPanel, checkBoxPanel);
        
        graphTabbedPanel = createTabbedPane();
        JPanel tabbedPanel = new JPanel();
        tabbedPanel.setSize(new Dimension(WIDTH_WIN, HEIGHT_GRAPH));
        tabbedPanel.add(graphTabbedPanel);
        
        mainPanel.add(optionPanel);
        mainPanel.add(tabbedPanel);
        
        this.pack();
        this.setTitle(BetApps.LABLES.getString("graph"));
        this.setLocationRelativeTo(getParent());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
    }
    
    private JTabbedPane createTabbedPane(){
        JTabbedPane out = new JTabbedPane();
        out.setMaximumSize(new Dimension(WIDTH_WIN, HEIGHT_GRAPH));
        out.setPreferredSize(new Dimension(WIDTH_WIN, HEIGHT_GRAPH));
        for(int i = 0; i < numOfFields; i++){
            out.addTab(bets[i] + " - " + bets[i+1], null);
        }
        return out;
    }
    
    private void repaintTabs(){
        setGraphPaintables();
        for(int i = 0; i < numOfFields; i++){
            graphTabbedPanel.setComponentAt(i, graphPainter.paintGraph(actualGraphs.get(i)));
        }
    }
    
    private void setGraphPaintables(){
        for(int i = 0; i < actualGraphs.size(); i++){
            for(int j = 0; j < actualGraphs.get(i).size(); j++){
                actualGraphs.get(i).get(j).setPaintAble(boxes[j].isSelected());
            }
        }
    }
    
    private void createCheckBoxes(){
        boxes = new JCheckBox[numOfStats];
        for(int i = 0; i < boxes.length; i++){
            boxes[i] = new JCheckBox(MainWin.textForStat[i]);
            boxes[i].setSelected(true);
            boxes[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    repaintTabs();
                }
            });
        }
    }
}