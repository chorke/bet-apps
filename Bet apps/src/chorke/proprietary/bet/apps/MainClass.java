
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.core.httpparsing.BetexplorerComMultithreadParser;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser.BettingSports;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.io.DBBetIOManager;
import chorke.proprietary.bet.apps.io.LoadProperties;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;



/**
 *
 * @author Chorke
 */
public class MainClass {
    
    public void start(){
        long start, end;
        
        DBBetIOManager man = new DBBetIOManager(StaticConstants.DATA_SOURCE);
        BetexplorerComMultithreadParser parser = new BetexplorerComMultithreadParser(man);
        start = System.nanoTime();
        parser.setExploredSport(BettingSports.All);
        parser.setStartDate(new GregorianCalendar(2013, Calendar.SEPTEMBER, 1));
        parser.setEndDate(new GregorianCalendar(2013, Calendar.SEPTEMBER, 4));
//        System.out.println(parser.getMatches().size());
        System.out.println("unsaved {" + parser.getUnsavedMatches().size()
                + "}: " + parser.getUnsavedMatches());
        System.out.println("matches {" + parser.getUndownloadedMatches().size()
                + "}: " + parser.getUndownloadedMatches());
        System.out.println("sports {" + parser.getUndownloadedSports().size()
                + "}: " + parser.getUndownloadedSports());
        end = System.nanoTime();
        printTime("downloading: ", end - start);
//        LoadProperties properties = new LoadProperties();
//        properties.addBetClass(Bet1x2.class);
//        properties.addBetClass(BetBothTeamsToScore.class);
//        properties.addBetClass(BetDoubleChance.class);
//        properties.addBetClass(BetDrawNoBet.class);
//        start = System.nanoTime();
//        Collection<Match> loadedMatches = man.loadMatches(properties);
//        System.out.println("loaded: " + loadedMatches.size());
//        end = System.nanoTime();
//        printTime("loading: ", end - start);
    }
    
    public static void printTime(String s, long nanoTime){
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
