
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.StaticConstants.Periode;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2Calculator;
import chorke.proprietary.bet.apps.core.calculators.YieldProperties;
import chorke.proprietary.bet.apps.core.graphs.GraphBuilderYield1x2;
import chorke.proprietary.bet.apps.core.httpparsing.BetexplorerComMultithreadParser;
import chorke.proprietary.bet.apps.StaticConstants.BettingSports;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.gui.GuiUtils;
import chorke.proprietary.bet.apps.gui.Season;
import chorke.proprietary.bet.apps.gui.panels.DownloadPanel;
import chorke.proprietary.bet.apps.gui.panels.GraphPanel;
import chorke.proprietary.bet.apps.gui.panels.GraphsCollectingPanel;
import chorke.proprietary.bet.apps.gui.panels.LoadingPanel;
import chorke.proprietary.bet.apps.gui.panels.MainPanel;
import chorke.proprietary.bet.apps.io.BetIOManager;
import chorke.proprietary.bet.apps.io.DBBetIOManager;
import chorke.proprietary.bet.apps.io.LoadProperties;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JFrame;
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
        DBBetIOManager man = new DBBetIOManager(StaticConstants.DATA_SOURCE);
        
        Season season = new Season();
//        season.setYieldProperties(new YieldProperties(new LinkedList<>(
//                Arrays.asList(new BigDecimal[]{new BigDecimal("1.22"),
//                new BigDecimal("1.23"), new BigDecimal("1.24"),
//                new BigDecimal("1.27"), new BigDecimal("1.28"),
//                new BigDecimal("1.25"), new BigDecimal("1.26")})),
//                "bet365"));
        season.setCalculator(new Yield1x2Calculator());
        season.setMatches(new LinkedList<Match>());
        season.setManager(man);
        season.setParser(new BetexplorerComMultithreadParser(null));
        season.setGraphBuilder(new GraphBuilderYield1x2());
        
//        season.setMatches(load(season.getManager()));
//        GraphsCollectingPanel panel = new GraphsCollectingPanel(season);
//        panel.setPreferredSize(new Dimension(400, 350));
//        panel.setMatches(matches);
//        panel.setProperties(yieldProp);
//        HTMLBetParser parser = new BetexplorerComMultithreadParser(null);
//        DownloadPanel panel = new DownloadPanel(parser);
//        panel.setPreferredSize(new Dimension(400, 150));
        
//        LoadProperties prop = new LoadProperties();
//        prop.addBetClass(BetAsianHandicap.class);
//        prop.addBetCompany("bet365");
//        LoadingPanel panel = new LoadingPanel(season);
        
        MainPanel panel = new MainPanel(season);
        
        JScrollPane pane = new JScrollPane(panel);
        pane.setPreferredSize(new Dimension(500, 400));
        GuiUtils.getDefaultFrame(null, JFrame.EXIT_ON_CLOSE,
                true, null, pane).setVisible(true);
        
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
    
    private Collection<Match> load(BetIOManager man){
        LoadProperties properties = new LoadProperties();
//        properties.addStartDate(new GregorianCalendar(2013, Calendar.OCTOBER, 20));
        properties.addBetClass(Bet1x2.class);
//        properties.addBetClass(BetBothTeamsToScore.class);
//        properties.addBetClass(BetDoubleChance.class);
//        properties.addBetClass(BetDrawNoBet.class);
        properties.addBetCompany("bet365");
        properties.addLeague("Slovakia", "");
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
