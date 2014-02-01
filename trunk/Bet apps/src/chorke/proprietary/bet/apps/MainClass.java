
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.core.calculators.CumulativeYieldProperties;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2Calculator;
import chorke.proprietary.bet.apps.core.calculators.YieldCalculator;
import chorke.proprietary.bet.apps.core.calculators.YieldProperties;
import chorke.proprietary.bet.apps.core.httpparsing.BetexplorerComMultithreadParser;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser.BettingSports;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.io.DBBetIOManager;
import chorke.proprietary.bet.apps.io.LoadProperties;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;



/**
 *
 * @author Chorke
 */
public class MainClass {
    
    public void start(){
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
        parser.setStartDate(new GregorianCalendar(2013, Calendar.JULY, 1));
        parser.setEndDate(new GregorianCalendar(2013, Calendar.JULY, 5));
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
        properties.addLeague("Premier League", "England");
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
        Map<Calendar, Yield1x2> yield = calculator.getPeriodicYieldWEEK(matches, yieldProp);
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
