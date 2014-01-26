
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetAsianHandicap;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.core.calculators.Yield;
import chorke.proprietary.bet.apps.core.calculators.Yield1x2;
import chorke.proprietary.bet.apps.core.httpparsing.BetexplorerComMultithreadParser;
import chorke.proprietary.bet.apps.core.httpparsing.HTMLBetParser.BettingSports;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import chorke.proprietary.bet.apps.core.match.score.Score;
import chorke.proprietary.bet.apps.core.match.sports.Sport;
import chorke.proprietary.bet.apps.io.BetIOException;
import chorke.proprietary.bet.apps.io.BetIOManager;
import chorke.proprietary.bet.apps.io.DBBetIOManager;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.postgresql.ds.PGPoolingDataSource;


/**
 *
 * @author Chorke
 */
public class MainClass {
    
    public void start(){
        List<Double> l = new ArrayList<>();
        l.add(new Double("1.01"));
        System.out.println(l.contains(new Double(1.01)));
        System.out.println(l.contains(new Double("1.01")));
        
        Match m = new Match(Sport.getSport(BettingSports.Soccer));
        MatchProperties mp = new MatchProperties();
        mp.setDate(new GregorianCalendar(2013, 11, 2, 10, 00, 00));
        MatchProperties mp1 = new MatchProperties();
        mp1.setDate(new GregorianCalendar(2013, 11, 2, 11, 00, 00));
        Match m1 = new Match(Sport.getSport(BettingSports.Soccer));
        m.setProperties(mp);
        m1.setProperties(mp1);
        System.out.println(m.compareTo(m1));
        System.out.println(m1.compareTo(m));
        
        List<Match> list = new LinkedList<>();
        list.add(m1);
        list.add(m);
        for(Match ma : list){
            System.out.println(ma.getProperties().getDate());
        }
        Collections.sort(list);
        for(Match ma : list){
            System.out.println(ma.getProperties().getDate());
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("name", new Object());
        System.out.println(map.containsKey("name"));
        map.remove("name");
        System.out.println(map.containsKey("name"));
        
        System.out.println(new BigDecimal("1.0").equals(BigDecimal.ONE));
        System.out.println(new BigDecimal("1.000001").toEngineeringString());
        System.out.println(new BigDecimal("-1.000001").toString());
        System.out.println(new BigDecimal("1.000001").toPlainString());
        System.out.println(new BigDecimal("-1.00000"));
        System.out.println(new BigDecimal("1.000001"));
        System.out.println(new BigDecimal("12312.1421"));
        
        
        Calendar cal = new GregorianCalendar(2013, Calendar.DECEMBER, 29);
        System.out.println(cal.get(Calendar.YEAR));
        System.out.println(cal.get(Calendar.WEEK_OF_YEAR));
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        System.out.println(cal.get(Calendar.YEAR));
        System.out.println(cal.get(Calendar.WEEK_OF_YEAR));
        
        int i = 1;
        System.out.println((long)i);
        i = 2;
        System.out.println((long)i);
        i = 3;
        System.out.println((long)i);
        i = 1286414;
        System.out.println((long)i);
        
        
//        DBBetIOManager man = new DBBetIOManager(StaticConstants.DATA_SOURCE);
//        for(Match toPrint : man.loadAllMatches()){
//            System.out.println(toPrint);
//            System.out.println("/////////////////////////////////////////////////////");
//        }
//        Match m = new Match(StaticConstants.Sport.Soccer);
//        Bet b = new Bet1x2("bet", 0, 0, 0);
//        Bet b1 = new BetOverUnder("bet", 10, 10, 10, "");
//        Bet b2 = new Bet1x2("mbet", 10, 20, 30);
//        Bet b3 = new BetAsianHandicap("xac", 1.89, 4.65, -0.75, "");
//        Bet b4 = new BetBothTeamsToScore("xxx", 1.99, 1.98);
//        Bet b5 = new BetDoubleChance("yyy", 1.01, 1.09, 1.1);
//        Bet b6 = new BetDrawNoBet("uuu", 3.43, 2.3);
//        MatchProperties prop = new MatchProperties();
//        prop.setCountry("Slovakia");
//        prop.setLeague("1. extra liga");
//        prop.setDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 2, 14, 0));
//        Score score = new Score();
//        score.setScoreFirstParty(1);
//        score.setScoreSecondParty(3);
//        score.addPartialScore(1, 0);
//        score.addPartialScore(0, 3);
//        
//        m.setProperties(prop);
//        m.setScore(score);
//        m.addBet(b);
//        m.addBet(b1);
//        m.addBet(b2);
//        m.addBet(b3);
//        m.addBet(b4);
//        m.addBet(b5);
//        m.addBet(b6);
//
//        PGPoolingDataSource ds = new PGPoolingDataSource();
//        ds.setPortNumber(5432);
//        ds.setServerName("localhost");
//        ds.setUser("Juraj Durani");
//        ds.setPassword("288charlotte462");
//        ds.setDatabaseName("BetAppsDB");
//        DBBetIOManager man = new DBBetIOManager(ds);
//        long start = System.nanoTime();
//        System.out.println(man);
//        System.out.println(man.cloneBetIOManager());
//        try{
//            man.saveMatch(m);
//        } catch (BetIOException |IllegalArgumentException ex){
//            System.err.println(ex);
//        }
//        long end = System.nanoTime();
//        System.out.println(end - start);
//        System.out.println(m.getBet("bet", Bet1x2.class));
//        System.out.println(Double.parseDouble("+0.5"));
//        BetexplorerComParser parser = new BetexplorerComParser();
//        parser.setActualProccessingDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 4));
//        parser.setExploredSport(Sport.Volleyball);
//        parser.printMatchesList();
//        parser.previousDay();
//        BetDownloader betD = new BetDownloader();
//        betD.setStartDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 1));
//        betD.setEndDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 1));
//        betD.getMatches();
//        System.out.println("start parsing");
//        BetexplorerComMultithreadParser parser = new BetexplorerComMultithreadParser(man);
//        parser.setExploredSport(BettingSports.All);
//        parser.setExploredSport(BettingSports.Hockey);
//        parser.setStartDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 4));
//        parser.setEndDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 4));
//        Collection<Match> matches = parser.getMatches();
//        System.out.println(matches.size());
//        System.out.println(matches);
//        System.out.println("matches");
//        System.out.println(parser.getUndownloadedMatches().size());
//        System.out.println("sports");
//        System.out.println(parser.getUndownloadedSports());
//        System.out.println("Saving to DB");
//        long start = System.nanoTime();
//        try{
//            man.saveMatches(matches);
//        } catch (BetIOException ex){
//            System.err.println(ex);
//        }
//        long end = System.nanoTime();
//        System.out.println(end - start);
//        parser.setExploredSport(Sport.Baseball);
//        parser.getMatches();
    }
}
