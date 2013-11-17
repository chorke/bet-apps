package bet.apps.windows;

import bet.apps.BetApps;
import bet.apps.core.DataReader;
import bet.apps.core.DateMiner;
import bet.apps.core.Match;
import bet.apps.filters.DateFilter;
import bet.apps.statistics.DayStatistics;
import bet.apps.statistics.MatchesStatistics;
import bet.apps.statistics.MonthStatistics;
import bet.apps.statistics.PeriodicStatistics;
import bet.apps.statistics.WeekStatistics;
import bet.apps.statistics.YearStatistics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MainWin extends JFrame {

    public static final int NUM_OF_FIELDS = 10;
    public static final int NUM_OF_STATS = 7; //at least 7
    public static final DateMiner DATE_MINER = new DateMiner();
    
    private MatchesStatistics statisticsAll;
    private MatchesStatistics filteredStatisticsAll;
    
    private PeriodicStatistics statisticsByDay;
    private PeriodicStatistics statisticsByWeek;
    private PeriodicStatistics statisticsByMonth;
    private PeriodicStatistics statisticsByYear;
    private double[] bets;
    
    private List<DateFilter> filters;
    
    private File lastFile = null;
    
    private JLabel statusBar = new JLabel();
    private JLabel[][] stat;
    private JPanel[] panelsForStat;
    private JTextField[] fieldForThresholds;
    private String[] textToFields = {"1.00", "1.15", "1.30", "1.45", "1.60", "1.70", "1.80",
        "1.90", "2.00", "2.10", "2.20"};
    public static final String[] textForStat = {"W", "x", "L", "1", "2", "-", "-", "-", "-", "-", "-", "m"};
    private static final String statusBarText = "W - na favorita     "
            + "L - proti favoritovi     "
            + "x - remíza     "
            + "1 - na domácich      "
            + "2 - na hostí     "
            + "m - počet zápasov     "
            + " |     Zápasov: ";
    
    public MainWin() {
        stat = new JLabel[NUM_OF_FIELDS][NUM_OF_STATS];
        panelsForStat = new JPanel[NUM_OF_FIELDS];
        fieldForThresholds = new JTextField[NUM_OF_FIELDS];
        bets = new double[NUM_OF_FIELDS + 1];
        statisticsAll = new MatchesStatistics();//NUM_OF_FIELDS, NUM_OF_STATS);

        filteredStatisticsAll = new MatchesStatistics();//NUM_OF_FIELDS, NUM_OF_STATS);
        statisticsByDay = new DayStatistics();
        statisticsByWeek = new WeekStatistics();
        statisticsByMonth = new MonthStatistics();
        statisticsByYear = new YearStatistics();

        filters = getDefaultFilters();

        createPanelForStatistics();
        createTextFieldsForThresholds();
        init();
    }

    public final void init() {

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(10, 5, 10, 5));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel);
        statusBar.setBorder(new EtchedBorder());
        setTextForStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        JButton addMatchesButton = new JButton(BetApps.LABLES.getString("addData"));
        addMatchesButton.setFocusable(false);
        addMatchesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MatchesStatistics ms = DataReader.loadBets(lastFile);
                lastFile = DataReader.getLastFile();
                addMoreMatches(ms);
                setTextForStatusBar();
//                System.out.println(BetApps.fileErrors);
                //DataReader.writeToArff(statisticsAll);
                //DataReader.writeToMLP(statisticsAll);
            }
        });

        JButton clearDataButton = new JButton(BetApps.LABLES.getString("clearData"));
        clearDataButton.setFocusable(false);
        clearDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statisticsAll.clearMatches();
                filteredStatisticsAll.clearMatches();
                statisticsByDay.clearMatches();
                statisticsByWeek.clearMatches();
                statisticsByMonth.clearMatches();
                statisticsByYear.clearMatches();
                setTextForStatusBar();
            }
        });
        
        JButton errorsButton = new JButton(BetApps.LABLES.getString("errors"));
        errorsButton.setFocusable(false);
        errorsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                BetErrors err = new BetErrors();
                err.setVisible(true);
            }
        });

        JButton statsButton = new JButton(BetApps.LABLES.getString("makeStats"));
        statsButton.setFocusable(false);
        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getAndSetBets();
                    filteredStatisticsAll.makeStats(bets, NUM_OF_STATS);
                    setLabels();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(getParent(), ex.getMessage());
                }
            }
        });

        JButton graphButton = new JButton(BetApps.LABLES.getString("makeGraph"));
        graphButton.setFocusable(false);
        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getAndSetBets();
                    GraphWin graphWindow = new GraphWin(statisticsByDay,
                            statisticsByWeek,
                            statisticsByMonth,
                            statisticsByYear,
                            bets);
                    graphWindow.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(getParent(), ex.getMessage());
                }
            }
        });

        JButton filterButton = new JButton(BetApps.LABLES.getString("filters"));
        filterButton.setFocusable(false);
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterWin filterWindow = new FilterWin(filters);
                filterWindow.setVisible(true);
            }
        });

        JButton applyFilterButton = new JButton(BetApps.LABLES.getString("applyFilters"));
        applyFilterButton.setFocusable(false);
        applyFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilters();
                setTextForStatusBar();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        JPanel dataPart = new JPanel();
        dataPart.setLayout(new BoxLayout(dataPart, BoxLayout.X_AXIS));
        dataPart.setBorder(new TitledBorder(new LineBorder(Color.GRAY), BetApps.LABLES.getString("data")));
        addToPanelWithGlue(dataPart, addMatchesButton, clearDataButton, errorsButton);
        JPanel filterPart = new JPanel();
        filterPart.setLayout(new BoxLayout(filterPart, BoxLayout.X_AXIS));
        filterPart.setBorder(new TitledBorder(new LineBorder(Color.GRAY), BetApps.LABLES.getString("filters")));
        addToPanelWithGlue(filterPart, filterButton, applyFilterButton);
        JPanel statsPart = new JPanel();
        statsPart.setLayout(new BoxLayout(statsPart, BoxLayout.X_AXIS));
        statsPart.setBorder(new TitledBorder(new LineBorder(Color.GRAY), BetApps.LABLES.getString("stats")));
        addToPanelWithGlue(statsPart, statsButton, graphButton);

        addToPanelWithGlue(buttonsPanel, dataPart, filterPart, statsPart);

        JPanel thresholdPanel = new JPanel();
        thresholdPanel.setLayout(new BoxLayout(thresholdPanel, BoxLayout.X_AXIS));
        addToPanelWithGlue(thresholdPanel, fieldForThresholds);

        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.X_AXIS));
        addToPanelWithGlue(statPanel, panelsForStat);

        mainPanel.add(buttonsPanel);
        mainPanel.add(thresholdPanel);
        mainPanel.add(statPanel);

        int width = 110 * NUM_OF_FIELDS;
        if (width < 550) {
            width = 550;
        }
        this.setSize(width, 250 + 15 * NUM_OF_STATS);
        this.setTitle("Bet Statistics");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    private void setLabels() {
        for (int i = 0; i < NUM_OF_FIELDS; i++) {
            stat[i][0].setText(bets[i] + " - " + bets[i + 1]);
            for (int j = 0; j < NUM_OF_STATS - 2; j++) {
                double result = filteredStatisticsAll.getYield(i, j);
                if (result > 0) {
                    stat[i][j + 1].setForeground(Color.GREEN.darker());
                } else if (result < 0) {
                    stat[i][j + 1].setForeground(Color.RED);
                } else {
                    stat[i][j + 1].setForeground(Color.BLACK);
                }
                stat[i][j + 1].setText(textForStat[j] + "  " + result + "%  ("
                        + filteredStatisticsAll.getNumOfMatchesInStatsAt(i, j)
                        + ")");
            }
            stat[i][NUM_OF_STATS - 1].setText(textForStat[textForStat.length - 1]
                    + "  "
                    + filteredStatisticsAll.getNumOfMatchesInFieldsAt(i));
        }
    }

    private void splitToGroups(int fromIndex) {
        if(fromIndex == 0){
            statisticsByDay.clearMatches();
            statisticsByWeek.clearMatches();
            statisticsByMonth.clearMatches();
            statisticsByYear.clearMatches();
        }
        List<Match> split = (List<Match>) filteredStatisticsAll.getMatches();
        split = split.subList(fromIndex, split.size());
        statisticsByDay.split(split);
        statisticsByWeek.split(split);
        statisticsByMonth.split(split);
        statisticsByYear.split(split);
    }

    private void getAndSetBets() throws NumberFormatException {
        if (bets != null && bets.length > 0) {
            bets[0] = Double.parseDouble(textToFields[0]);
            for (int i = 0; i < fieldForThresholds.length; i++) {
                bets[i + 1] = Double.parseDouble(fieldForThresholds[i].getText());
            }
        }
    }

    public static void addToPanelWithGlue(JPanel panel, JComponent... comp) {
        if (panel != null && comp != null) {
            for (int i = 0; i < comp.length; i++) {
                panel.add(Box.createGlue());
                panel.add(comp[i]);
            }
            panel.add(Box.createGlue());
        }
    }

    private void createPanelForStatistics() {
        for (int i = 0; i < panelsForStat.length; i++) {
            panelsForStat[i] = new JPanel();
            panelsForStat[i].setLayout(new BoxLayout(panelsForStat[i], BoxLayout.Y_AXIS));
            panelsForStat[i].setBorder(
                    new TitledBorder(new LineBorder(Color.GRAY),
                    Integer.toString(i + 1)));
            stat[i][0] = new JLabel(textToFields[i] + " - " + textToFields[i + 1]);
            panelsForStat[i].add(stat[i][0]);
            for (int j = 1; j < NUM_OF_STATS; j++) {
                if (j == NUM_OF_STATS - 1) {
                    stat[i][j] = new JLabel(textForStat[textForStat.length - 1]);
                } else {
                    stat[i][j] = new JLabel(textForStat[j - 1]);
                }
                panelsForStat[i].add(stat[i][j]);
            }
        }
    }

    private void createTextFieldsForThresholds() {
        Dimension d = new Dimension(40, 20);
        for (int i = 0; i < fieldForThresholds.length; i++) {
            fieldForThresholds[i] = new JTextField(textToFields[i + 1]);
            fieldForThresholds[i].setMaximumSize(d);
            fieldForThresholds[i].setPreferredSize(d);
        }
    }

    private void applyFilters(){
        filteredStatisticsAll.clearMatches();
        filteredStatisticsAll.addMatch(statisticsAll.getMatches());
        filteredStatisticsAll.applyFilters(filters);
        splitToGroups(0);
    }
    
    /**
     * Add elemetns from {@code statisticsALl} to {@code filteredStatisticsAll}.
     * Starts at index {@code startSize}.
     * Returns index of first element that was added from {@code statisticsAll}.
     * 
     * @param startSize index of first element to be added
     * @return index of first element that was added from {@code statisticsAll}
     */
//    private int setFilteredStatisticsAll(int startSize){
//        if(startSize == 0){
//            filteredStatisticsAll.clearMatches();
//            filteredStatisticsAll.addMatch(statisticsAll.getMatches());
//            return 0;
//        } else {
//            int endSize = statisticsAll.getMatches().size();
//            int output = filteredStatisticsAll.getMatches().size();
//            filteredStatisticsAll.addMatch(statisticsAll.getMatches().subList(startSize, endSize));
//            return output;
//        }
//    }
    
    private void setTextForStatusBar() {
        statusBar.setText(statusBarText + filteredStatisticsAll.getMatches().size());
    }
    
    private void addMoreMatches(MatchesStatistics matches){
        statisticsAll.addMatch(matches.getMatches());
        int startSize = filteredStatisticsAll.getMatches().size();
        filteredStatisticsAll.addMatch(matches.getMatches());
        filteredStatisticsAll.applyFilters(filters, startSize);
        splitToGroups(startSize);
    }
    
    private List<DateFilter> getDefaultFilters(){
        List<DateFilter> result = new ArrayList<>();
        
//        DateFilter weekend = new DateFilter("Víkend");
//        weekend.setType(DateFilter.FilterType.DAY_OF_WEEK);
//        weekend.setRange(DateFilter.FilterRange.FROM_TO);
//        weekend.setStart(DATE_MINER.getDate("05_01_2013"));
//        weekend.setEnd(DATE_MINER.getDate("06_01_2013"));
//        result.add(weekend);
//        
//        DateFilter week = new DateFilter("Pracovné dni");
//        week.setType(DateFilter.FilterType.DAY_OF_WEEK);
//        week.setRange(DateFilter.FilterRange.FROM_TO);
//        week.setStart(DATE_MINER.getDate("07_01_2013"));
//        week.setEnd(DATE_MINER.getDate("11_01_2013"));
//        result.add(week);
        
        return result;
    }
}