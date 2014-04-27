
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.StaticConstants.BetPossibility;
import chorke.proprietary.bet.apps.StaticConstants.Periode;
import chorke.proprietary.bet.apps.core.graphs.Graph;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.core.calculators.CumulativeYieldProperties;
import chorke.proprietary.bet.apps.core.calculators.Yield;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2Calculator;
import chorke.proprietary.bet.apps.core.calculators.YieldCalculator;
import chorke.proprietary.bet.apps.core.calculators.YieldProperties;
import chorke.proprietary.bet.apps.core.graphs.GraphBuilderYield1x2;
import chorke.proprietary.bet.apps.core.httpparsing.BetexplorerComMultithreadParser;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser.BettingSports;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.gui.panels.DownloadPanel;
import chorke.proprietary.bet.apps.gui.panels.GraphPanel;
import chorke.proprietary.bet.apps.gui.panels.GraphsCollectingPanel;
import chorke.proprietary.bet.apps.io.CloneableBetIOManager;
import chorke.proprietary.bet.apps.io.DBBetIOManager;
import chorke.proprietary.bet.apps.io.LoadProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



/**
 *
 * @author Chorke
 */
public class MainClass {
    
    public void start(){
//        betsDownloading();
        guiTester();
    }
    
    private void guiTester(){
//        YieldProperties yieldProp = new YieldProperties();
//        yieldProp.setBetCompany("bet365");
//        yieldProp.addScale(new BigDecimal("1.60"));
//        yieldProp.addScale(new BigDecimal("1.70"));
//        yieldProp.addScale(new BigDecimal("2.00"));
//        
//        Yield1x2Calculator calcul = new Yield1x2Calculator();
        CloneableBetIOManager man = new DBBetIOManager(StaticConstants.DATA_SOURCE);
//        Collection<Match> matches = load(max);
//        Graph g = new GraphBuilderYield1x2().getGraph(
//                calcul.getPeriodicYield(matches,
//                    yieldProp, Periode.Month),
//                BetPossibility.Tie, 0);
//        GraphPanel p = new GraphPanel(g);
//        p.setPreferredSize(new Dimension(500, 300));
//        GraphsCollectingPanel panel = new GraphsCollectingPanel(calcul, new GraphBuilderYield1x2());
        HTMLBetParser parser = new BetexplorerComMultithreadParser(man);
        DownloadPanel panel = new DownloadPanel(parser);
        JScrollPane pane = new JScrollPane(panel);
        panel.setPreferredSize(new Dimension(400, 150));
        pane.setPreferredSize(new Dimension(410, 160));
        JFrame f = new JFrame();
        f.add(pane);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
//        panel.setMatches(matches);
//        panel.setProperties(yieldProp);
    }
    
    private void betsDownloading(){
        long start, end;
        
        DBBetIOManager man = new DBBetIOManager(StaticConstants.DATA_SOURCE);
        Collection<Match> loaded;
        
        start = System.nanoTime();
//        download(man);
        end = System.nanoTime();
        printTime("downloading: ", end - start);
        
        start = end;
//        loaded = load(man);
        end = System.nanoTime();
        printTime("loading: ", end - start);
        
        start = end;
//        count(loaded);
        end = System.nanoTime();
        printTime("calculator: ", end - start);
    }
    
    private void download(DBBetIOManager man){
        BetexplorerComMultithreadParser parser = new BetexplorerComMultithreadParser(man);
        parser.setExploredSport(BettingSports.All);
        parser.setStartDate(new GregorianCalendar(2012, Calendar.NOVEMBER, 1));
        parser.setEndDate(new GregorianCalendar(2012, Calendar.NOVEMBER, 4));
        System.out.println(parser.getMatches().size());
        System.out.println("unsaved {" + parser.getUnsavedMatches().size()
                + "}: " + parser.getUnsavedMatches());
        System.out.println("matches {" + parser.getUndownloadedMatches().size()
                + "}: " + parser.getUndownloadedMatches());
        System.out.println("sports {" + parser.getUndownloadedSports().size()
                + "}: " + parser.getUndownloadedSports());
    }
    
    private Collection<Match> load(DBBetIOManager man){
        LoadProperties properties = new LoadProperties();
//        properties.addStartDate(new GregorianCalendar(2013, Calendar.OCTOBER, 20));
        properties.addBetClass(Bet1x2.class);
//        properties.addBetClass(BetBothTeamsToScore.class);
//        properties.addBetClass(BetDoubleChance.class);
//        properties.addBetClass(BetDrawNoBet.class);
        properties.addBetCompany("bet365");
        properties.addLeague("", "Slovakia");
        Collection<Match> matches = man.loadMatches(properties);
        System.out.println("loaded: " + matches.size());
        return matches;
    }
    
    private void count(Collection<Match> matches){
        Yield1x2Calculator calculator = new Yield1x2Calculator();
        YieldProperties yieldProp = new YieldProperties();
        yieldProp.setBetCompany("bet365");
        yieldProp.addScale(new BigDecimal("1.60"));
        yieldProp.addScale(new BigDecimal("1.70"));
        yieldProp.addScale(new BigDecimal("2.00"));
        Map<Calendar, Yield1x2> yield = calculator.getPeriodicYield(matches, yieldProp, Periode.Week);
        for(Calendar cal : yield.keySet()){
            System.out.println(cal.get(Calendar.WEEK_OF_YEAR) + ":  " + yield.get(cal));
        }
    }
    
    private static void printTime(String s, long nanoTime){
        long withoutNano = (nanoTime/1000);
        long nano = nanoTime - (withoutNano * 1000);
        long withoutMicro = withoutNano/1000;
        long micro = withoutNano - (withoutMicro * 1000);
        long withoutMili = withoutMicro/1000;
        long mili = withoutMicro - (withoutMili * 1000);
        String str = s + withoutMili + "s  " + mili + "ms  " + micro + "us  " + nano + "ns";
        System.out.println(str);
    }
}
