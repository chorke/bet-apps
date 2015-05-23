
package chorke.bet.apps.io;

import static chorke.bet.apps.BasicTests.assertAreBothNull;
import static chorke.bet.apps.BasicTests.collectionChecker;
import chorke.bet.apps.core.CoreUtils.BettingSports;
import chorke.bet.apps.core.bets.Bet;
import chorke.bet.apps.core.bets.Bet1x2;
import chorke.bet.apps.core.bets.BetAsianHandicap;
import chorke.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.bet.apps.core.bets.BetDoubleChance;
import chorke.bet.apps.core.bets.BetDrawNoBet;
import chorke.bet.apps.core.bets.BetOverUnder;
import chorke.bet.apps.core.match.Match;
import chorke.bet.apps.core.match.MatchProperties;
import chorke.bet.apps.core.match.sports.Sport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Chorke
 */
public class BetIOManagerTest {
    
    private static Connection connection;
    private static BetIOManager testingManager;
    
    private static Match match1, match2, match3, match4;
    private static Bet1x2 bet1x2_Tipsport_Match3, 
            bet1x2_Tipsport_Match1, 
            bet1x2_Fortuna_Match1, 
            bet1x2_Bet365_Match1,
            bet1x2_Tipsport_Match2,
            bet1x2_Bet365_Match2,
            bet1x2_Fortuna_Match2;
    private static BetAsianHandicap betah_Bet365_Plus1p0_Match3,
            betah_Bet365_Minus0p5_Match1,
            betah_Bet365_Minus0p75_Match1,
            betah_Fortuna_Minus0p5_Match1,
            betah_Fortuna_Minus0p75_Match1,
            betah_Doublebet_Plus1p5_Match1,
            betah_Doublebet_Plus0p75_Match1,
            betah_Bet365_Plus0p75_Match2,
            betah_Bet365_Plus1p0_Match2;
    private static BetBothTeamsToScore betbtts_Fortuna_Match3,
            betbtts_Fortuna_Match4,
            betbtts_Bet365_Match4;
    private static BetDoubleChance betdc_Tipsport_Match3,
            betdc_Bet365_Match1;
    private static BetDrawNoBet betdnb_Tipsport_Match3;
    private static BetOverUnder betou_Bet365_1p5_Match3,
            betou_Tipsport_1p0_Match1,
            betou_Tipsport_1p5_Match1,
            betou_Fortuna_1p5_Match1;
    
    private static MatchProperties prop1;
    private static MatchProperties prop2;
    private static MatchProperties prop3;
    private static MatchProperties prop4;
    
    @BeforeClass
    @SuppressWarnings("ConvertToTryWithResources")
    public static void initClass(){
        try{
            connection = DriverManager.getConnection("jdbc:derby:memory:testDB;create=true");
            
            EmbeddedDataSource ds = new EmbeddedDataSource();
            ds.setDatabaseName("memory:testDB");
            testingManager = new DBBetIOManager(ds);
            
            Statement st = connection.createStatement();
            st.addBatch("CREATE TABLE matches(" +
                "id integer GENERATED ALWAYS AS IDENTITY," +
                "sport varchar(20)," +
                "country varchar(50)," +
                "league varchar(50)," +
                "matchdate timestamp," +
                "PRIMARY KEY (id))");
            st.addBatch("CREATE TABLE scores(" +
                "matchid integer NOT NULL," +
                "team1 integer," +
                "team2 integer," +
                "part integer NOT NULL," +
                "PRIMARY KEY (matchid, part))");
            st.addBatch("CREATE TABLE bet1x2(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "bet1 numeric(7,2)," +
                "betx numeric(7,2)," +
                "bet2 numeric(7,2)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betah(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "bet1 numeric(7,2)," +
                "bet2 numeric(7,2)," +
                "handicap numeric(5,2) NOT NULL," +
                "description varchar(20) NOT NULL," +
                "PRIMARY KEY (matchid, betcompany, handicap, description))");
            st.addBatch("CREATE TABLE betbtts(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "yesbet numeric(7,2)," +
                "nobet numeric(7,2)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betdc(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "bet1x numeric(7,2)," +
                "bet2x numeric(7,2)," +
                "bet12 numeric(7,2)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betdnb(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "bet1 numeric(7,2)," +
                "bet2 numeric(7,2)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betou(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "total numeric(5,2) NOT NULL," +
                "overbet numeric(7,2)," +
                "underbet numeric(7,2)," +
                "description varchar(50) NOT NULL," +
                "PRIMARY KEY (matchid, betcompany, total, description))");
            st.executeBatch();
            st.close();
        } catch (SQLException ex){
            System.out.println("BEFORE CLASS: " + ex);
            connection = null;
        }
        match1 = new Match(Sport.getSport(Sport.SOCCER_STRING));
        match2 = new Match(Sport.getSport(Sport.HOCKEY_STRING));
        match3 = new Match(Sport.getSport(Sport.BASKETBALL_STRING));
        match4 = new Match(Sport.getSport(Sport.SOCCER_STRING));
        
        prop1 = new MatchProperties();
        prop2 = new MatchProperties();
        prop3 = new MatchProperties();
        prop4 = new MatchProperties();
        
        prop1.setCountry("Slovensko");
        prop1.setLeague("Corgoň liga");
        prop1.setDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 1));
        
        prop2.setCountry("Slovensko");
        prop2.setLeague("Extraliga");
        prop2.setDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        
        prop3.setCountry("Česká republika");
        prop3.setLeague("1. liga");
        prop3.setDate(new GregorianCalendar(2013, Calendar.DECEMBER, 31));
        
        prop4.setCountry("USA");
        prop4.setLeague("2. league");
        prop4.setDate(new GregorianCalendar(2014, Calendar.JANUARY, 25));
        
        match1.setProperties(prop1);
        match2.setProperties(prop2);
        match3.setProperties(prop3);
        match4.setProperties(prop4);
        
        match1.addPartialScore(0, 0);
        match1.addPartialScore(0, 0);
        
        match2.addPartialScore(1, 0);
        match2.addPartialScore(1, 0);
        match2.addPartialScore(1, 2);
        
        match3.addPartialScore(18, 13);
        match3.addPartialScore(19, 24);
        match3.addPartialScore(23, 23);
        match3.addPartialScore(17, 20);
        
        match4.addPartialScore(0, 0);
        match4.addPartialScore(1, 2);
        
        bet1x2_Bet365_Match1 = new Bet1x2("bet365", 
                new BigDecimal("1.03"), new BigDecimal("3.99"), new BigDecimal("4.90"));
        bet1x2_Tipsport_Match1 = new Bet1x2("tipsport", 
                new BigDecimal("1.01"), new BigDecimal("4.87"), new BigDecimal("5.76"));
        bet1x2_Fortuna_Match1 = new Bet1x2("fortuna", 
                new BigDecimal("1.05"), new BigDecimal("4.88"), new BigDecimal("4.98"));
        betah_Bet365_Minus0p5_Match1 = new BetAsianHandicap("bet365", 
                new BigDecimal("1.03"), new BigDecimal("3.89"), new BigDecimal("-0.50"), "");
        betah_Bet365_Minus0p75_Match1 = new BetAsianHandicap("bet365", 
                new BigDecimal("1.10"), new BigDecimal("2.89"), new BigDecimal("-0.75"), "hadicap");
        betah_Fortuna_Minus0p5_Match1 = new BetAsianHandicap("fortuna", 
                new BigDecimal("1.03"), new BigDecimal("3.99"), new BigDecimal("-0.50"), "");
        betah_Fortuna_Minus0p75_Match1 = new BetAsianHandicap("fortuna", 
                new BigDecimal("1.10"), new BigDecimal("2.99"), new BigDecimal("-0.75"), "");
        betah_Doublebet_Plus1p5_Match1 = new BetAsianHandicap("doublebet", 
                new BigDecimal("1.03"), new BigDecimal("3.43"), new BigDecimal("1.50"), "nohandicap");
        betah_Doublebet_Plus0p75_Match1 = new BetAsianHandicap("doublebet", 
                new BigDecimal("1.10"), new BigDecimal("2.74"), new BigDecimal("0.75"), "");
        betdc_Bet365_Match1 = new BetDoubleChance("bet365", 
                new BigDecimal("1.13"), new BigDecimal("1.74"), new BigDecimal("1.75"));
        betou_Tipsport_1p0_Match1 = new BetOverUnder("tipsport", 
                new BigDecimal("1.00"), new BigDecimal("2.74"), new BigDecimal("2.75"), "");
        betou_Tipsport_1p5_Match1 = new BetOverUnder("tipsport", 
                new BigDecimal("1.50"), new BigDecimal("2.74"), new BigDecimal("1.75"), "");
        betou_Fortuna_1p5_Match1 = new BetOverUnder("fortuna", 
                new BigDecimal("1.50"), new BigDecimal("2.75"), new BigDecimal("1.75"), "");
        
        match1.addBet(bet1x2_Tipsport_Match1);
        match1.addBet(bet1x2_Fortuna_Match1);
        match1.addBet(bet1x2_Bet365_Match1);
        match1.addBet(betah_Bet365_Minus0p5_Match1);
        match1.addBet(betah_Bet365_Minus0p75_Match1);
        match1.addBet(betah_Fortuna_Minus0p5_Match1);
        match1.addBet(betah_Fortuna_Minus0p75_Match1);
        match1.addBet(betah_Doublebet_Plus1p5_Match1);
        match1.addBet(betah_Doublebet_Plus0p75_Match1);
        match1.addBet(betdc_Bet365_Match1);
        match1.addBet(betou_Tipsport_1p0_Match1);
        match1.addBet(betou_Tipsport_1p5_Match1);
        match1.addBet(betou_Fortuna_1p5_Match1);
        
        bet1x2_Tipsport_Match2 = new Bet1x2("tipsport", 
                new BigDecimal("1.10"), new BigDecimal("2.74"), new BigDecimal("1.75"));
        bet1x2_Bet365_Match2 = new Bet1x2("bet365", 
                new BigDecimal("1.11"), new BigDecimal("2.70"), new BigDecimal("1.65"));
        bet1x2_Fortuna_Match2 = new Bet1x2("fortuna", 
                new BigDecimal("1.21"), new BigDecimal("2.70"), new BigDecimal("1.60"));
        betah_Bet365_Plus0p75_Match2 = new BetAsianHandicap("bet365",
                new BigDecimal("1.10"), new BigDecimal("2.74"), new BigDecimal("0.75"), "");
        betah_Bet365_Plus1p0_Match2 = new BetAsianHandicap("bet365",
                new BigDecimal("1.56"), new BigDecimal("1.74"), new BigDecimal("1.00"), "");
        
        match2.addBet(bet1x2_Tipsport_Match2);
        match2.addBet(bet1x2_Bet365_Match2);
        match2.addBet(bet1x2_Fortuna_Match2);
        match2.addBet(betah_Bet365_Plus0p75_Match2);
        match2.addBet(betah_Bet365_Plus1p0_Match2);
        
        bet1x2_Tipsport_Match3 = new Bet1x2("tipsport", 
                new BigDecimal("1.56"), new BigDecimal("1.74"), new BigDecimal("1.01"));
        betdc_Tipsport_Match3 = new BetDoubleChance("tipsport",
                new BigDecimal("1.56"), new BigDecimal("1.88"), new BigDecimal("1.97"));
        betdnb_Tipsport_Match3 = new BetDrawNoBet("tipsport",
                new BigDecimal("1.88"), new BigDecimal("1.95"));
        betbtts_Fortuna_Match3 = new BetBothTeamsToScore("fortuna",
                new BigDecimal("1.56"), new BigDecimal("1.97"));
        betah_Bet365_Plus1p0_Match3 = new BetAsianHandicap("bet365", 
                new BigDecimal("1.56"), new BigDecimal("1.74"), new BigDecimal("1.00"), "nohan");
        betou_Bet365_1p5_Match3 = new BetOverUnder("bet365", 
                new BigDecimal("1.50"), new BigDecimal("1.74"), new BigDecimal("1.65"), "yeshan");
        
        match3.addBet(bet1x2_Tipsport_Match3);
        match3.addBet(betdc_Tipsport_Match3);
        match3.addBet(betdnb_Tipsport_Match3);
        match3.addBet(betbtts_Fortuna_Match3);
        match3.addBet(betah_Bet365_Plus1p0_Match3);
        match3.addBet(betou_Bet365_1p5_Match3);
        
        betbtts_Fortuna_Match4 = new BetBothTeamsToScore("fortuna",
                new BigDecimal("1.56"), new BigDecimal("1.74"));
        betbtts_Bet365_Match4 = new BetBothTeamsToScore("bet365",
                new BigDecimal("1.82"), new BigDecimal("1.67"));
        
        match4.addBet(betbtts_Fortuna_Match4);
        match4.addBet(betbtts_Bet365_Match4);
    }
    
    @AfterClass
    public static void tearDownClass() throws SQLException{
        try(Statement st = connection.createStatement()){
            st.addBatch("DROP TABLE matches");
            st.addBatch("DROP TABLE scores");
            st.addBatch("DROP TABLE bet1x2");
            st.addBatch("DROP TABLE betah");
            st.addBatch("DROP TABLE betbtts");
            st.addBatch("DROP TABLE betdc");
            st.addBatch("DROP TABLE betdnb");
            st.addBatch("DROP TABLE betou");
            st.executeBatch();
            connection.close();
            connection = null;
            testingManager = null;
        } catch (SQLException ex){
            System.out.println("AFTER CLASS: " + ex);
        }
        match1 = null;
        match2 = null;
        match3 = null;
        match4 = null;
    }
    
    @After
    public void tearDownTest(){
        try(Statement st = connection.createStatement()){
            st.addBatch("DELETE FROM matches");
            st.addBatch("DELETE FROM scores");
            st.addBatch("DELETE FROM bet1x2");
            st.addBatch("DELETE FROM betah");
            st.addBatch("DELETE FROM betbtts");
            st.addBatch("DELETE FROM betdc");
            st.addBatch("DELETE FROM betdnb");
            st.addBatch("DELETE FROM betou");
            st.executeBatch();
        } catch (SQLException ex){
            System.out.println("AFTER: " + ex);
        }
        match1.setId(null);
        match2.setId(null);
        match3.setId(null);
        match4.setId(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saveTestNull(){
        testingManager.saveMatch(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saveTestNullSport(){
        testingManager.saveMatch(new Match(null));
    }
    
    @Test
    public void saveTestIllegalProperties(){
        Match m = new Match(Sport.getSport(Sport.SOCCER_STRING));
        MatchProperties mp = new MatchProperties();
        m.setProperties(mp);
        try{
            testingManager.saveMatch(m);
            fail("No exception (null country, null league)");
        } catch (IllegalArgumentException ex){
        } catch (Exception ex){ fail("Bad exception (null country, null league): " + ex); }
        mp.setCountry(null);
        mp.setLeague("");
        try{
            testingManager.saveMatch(m);
            fail("No exception (null country, empty league)");
        } catch (IllegalArgumentException ex){
        } catch (Exception ex){ fail("Bad exception (null country, empty league): " + ex); }
        mp.setCountry("");
        mp.setLeague(null);
        try{
            testingManager.saveMatch(m);
            fail("No exception (empty country, null league)");
        } catch (IllegalArgumentException ex){
        } catch (Exception ex){ fail("Bad exception (empty country, null league): " + ex); }
        mp.setCountry("");
        mp.setLeague("");
        try{
            testingManager.saveMatch(m);
            fail("No exception (empty country, empty league)");
        } catch (IllegalArgumentException ex){
        } catch (Exception ex){ fail("Bad exception (empty country, empty league): " + ex); }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saveTestNonNullID(){
        Match m = new Match(Sport.getSport(Sport.HOCKEY_STRING));
        MatchProperties mp = new MatchProperties();
        mp.setCountry("a");
        mp.setLeague("b");
        m.setProperties(mp);
        m.setId(1L);
        testingManager.saveMatch(m);
    }
    
    @Test
    public void saveTestFullMatch(){
        testingManager.saveMatch(match3);
        try(PreparedStatement match = connection.prepareStatement("SELECT * FROM matches");
                PreparedStatement score = connection.prepareStatement("SELECT * FROM scores");
                PreparedStatement bet1x2 = connection.prepareStatement("SELECT * FROM bet1x2");
                PreparedStatement betah = connection.prepareStatement("SELECT * FROM betah");
                PreparedStatement betbtts = connection.prepareStatement("SELECT * FROM betbtts");
                PreparedStatement betdc = connection.prepareStatement("SELECT * FROM betdc");
                PreparedStatement betdnb = connection.prepareStatement("SELECT * FROM betdnb");
                PreparedStatement betou = connection.prepareStatement("SELECT * FROM betou")){
            ResultSet result = match.executeQuery();
            if(!result.next()){
                fail("Unsaved match");
            }
            assertEquals(result.getString("sport"), match3.getSport().toString());
            assertEquals(result.getString("country"), match3.getProperties().getCountry());
            assertEquals(result.getString("league"), match3.getProperties().getLeague());
            assertEquals(result.getTimestamp("matchdate"), new Timestamp(
                    match3.getProperties().getDate().getTimeInMillis()));
            if(result.next()){
                fail("More then one result (match)");
            }
            
            result = score.executeQuery();
            for(int i = 0; i < 5; i++){
                if(!result.next()){
                    fail("Less then 5 saved scores");
                }
                assertEquals((Long)result.getLong("matchid"), match3.getId());
                if(result.getInt("part") == -1){
                    assertEquals(result.getInt("team1"), 77);
                    assertEquals(result.getInt("team2"), 80);
                } else if(result.getInt("part") == 1){
                    assertEquals(result.getInt("team1"), 18);
                    assertEquals(result.getInt("team2"), 13);
                } else if(result.getInt("part") == 2){
                    assertEquals(result.getInt("team1"), 19);
                    assertEquals(result.getInt("team2"), 24);
                } else if(result.getInt("part") == 3){
                    assertEquals(result.getInt("team1"), 23);
                    assertEquals(result.getInt("team2"), 23);
                } else if(result.getInt("part") == 4){
                    assertEquals(result.getInt("team1"), 17);
                    assertEquals(result.getInt("team2"), 20);
                } else {
                    fail("Bad score found");
                }
            }
            if(result.next()){
                fail("More then 5 results (score)");
            }
            
            result = bet1x2.executeQuery();
            if(!result.next()){
                fail("Unsaved bet 1x2");
            }
            assertEquals((Long)result.getLong("matchid"), match3.getId());
            assertEquals(result.getString("betcompany"), bet1x2_Tipsport_Match3.betCompany);
            assertEquals(result.getString("bet1"), bet1x2_Tipsport_Match3.bet1.toPlainString());
            assertEquals(result.getString("bet2"), bet1x2_Tipsport_Match3.bet2.toPlainString());
            assertEquals(result.getString("betx"), bet1x2_Tipsport_Match3.betX.toPlainString());
            if(result.next()){
                fail("More then one result (bet1x2)");
            }
            
            result = betah.executeQuery();
            if(!result.next()){
                fail("Unsaved bet ah");
            }
            assertEquals((Long)result.getLong("matchid"), match3.getId());
            assertEquals(result.getString("betcompany"), betah_Bet365_Plus1p0_Match3.betCompany);
            assertEquals(result.getString("bet1"), betah_Bet365_Plus1p0_Match3.bet1.toPlainString());
            assertEquals(result.getString("bet2"), betah_Bet365_Plus1p0_Match3.bet2.toPlainString());
            assertEquals(result.getString("description"), betah_Bet365_Plus1p0_Match3.description);
            assertEquals(result.getString("handicap"), betah_Bet365_Plus1p0_Match3.handicap.toPlainString());
            if(result.next()){
                fail("More then one result");
            }
            
            result = betbtts.executeQuery();
            if(!result.next()){
                fail("Unsaved bet btts");
            }
            assertEquals((Long)result.getLong("matchid"), match3.getId());
            assertEquals(result.getString("betcompany"), betbtts_Fortuna_Match3.betCompany);
            assertEquals(result.getString("yesbet"), betbtts_Fortuna_Match3.yes.toPlainString());
            assertEquals(result.getString("nobet"), betbtts_Fortuna_Match3.no.toPlainString());
            if(result.next()){
                fail("More then one result");
            }
            
            result = betdc.executeQuery();
            if(!result.next()){
                fail("Unsaved bet dc");
            }
            assertEquals((Long)result.getLong("matchid"), match3.getId());
            assertEquals(result.getString("betcompany"), betdc_Tipsport_Match3.betCompany);
            assertEquals(result.getString("bet12"), betdc_Tipsport_Match3.bet12.toPlainString());
            assertEquals(result.getString("bet1x"), betdc_Tipsport_Match3.bet1X.toPlainString());
            assertEquals(result.getString("bet2x"), betdc_Tipsport_Match3.betX2.toPlainString());
            if(result.next()){
                fail("More then one result");
            }
            
            result = betdnb.executeQuery();
            if(!result.next()){
                fail("Unsaved bet dnb");
            }
            assertEquals((Long)result.getLong("matchid"), match3.getId());
            assertEquals(result.getString("betcompany"), betdnb_Tipsport_Match3.betCompany);
            assertEquals(result.getString("bet1"), betdnb_Tipsport_Match3.bet1.toPlainString());
            assertEquals(result.getString("bet2"), betdnb_Tipsport_Match3.bet2.toPlainString());
            if(result.next()){
                fail("More then one result");
            }
            
            result = betou.executeQuery();
            if(!result.next()){
                fail("Unsaved bet ou");
            }
            assertEquals((Long)result.getLong("matchid"), match3.getId());
            assertEquals(result.getString("betcompany"), betou_Bet365_1p5_Match3.betCompany);
            assertEquals(result.getString("description"), betou_Bet365_1p5_Match3.description);
            assertEquals(result.getString("overbet"), betou_Bet365_1p5_Match3.over.toPlainString());
            assertEquals(result.getString("total"), betou_Bet365_1p5_Match3.total.toPlainString());
            assertEquals(result.getString("underbet"), betou_Bet365_1p5_Match3.under.toPlainString());
            if(result.next()){
                fail("More then one result");
            }
        } catch (SQLException ex){
            fail("Error in SQL statements: " + ex);
        }
    }
    
    @Test
    public void saveTestNonFullMatch(){
        testingManager.saveMatch(match4);
        try(PreparedStatement match = connection.prepareStatement("SELECT * FROM matches");
                PreparedStatement score = connection.prepareStatement("SELECT * FROM scores");
                PreparedStatement bet1x2 = connection.prepareStatement("SELECT * FROM bet1x2");
                PreparedStatement betah = connection.prepareStatement("SELECT * FROM betah");
                PreparedStatement betbtts = connection.prepareStatement("SELECT * FROM betbtts");
                PreparedStatement betdc = connection.prepareStatement("SELECT * FROM betdc");
                PreparedStatement betdnb = connection.prepareStatement("SELECT * FROM betdnb");
                PreparedStatement betou = connection.prepareStatement("SELECT * FROM betou")){
            ResultSet result = match.executeQuery();
            if(!result.next()){
                fail("Unsaved match");
            }
            assertEquals(result.getString("sport"), match4.getSport().toString());
            assertEquals(result.getString("country"), match4.getProperties().getCountry());
            assertEquals(result.getString("league"), match4.getProperties().getLeague());
            assertEquals(result.getTimestamp("matchdate"), new Timestamp(
                    match4.getProperties().getDate().getTimeInMillis()));
            if(result.next()){
                fail("More then one result (match)");
            }
            
            result = score.executeQuery();
            for(int i = 0; i < 3; i++){
                if(!result.next()){
                    fail("Less then 3 saved scores");
                }
                assertEquals((Long)result.getLong("matchid"), match4.getId());
                if(result.getInt("part") == -1){
                    assertEquals(result.getInt("team1"), 1);
                    assertEquals(result.getInt("team2"), 2);
                } else if(result.getInt("part") == 1){
                    assertEquals(result.getInt("team1"), 0);
                    assertEquals(result.getInt("team2"), 0);
                } else if(result.getInt("part") == 2){
                    assertEquals(result.getInt("team1"), 1);
                    assertEquals(result.getInt("team2"), 2);
                } else {
                    fail("Bad score found");
                }
            }
            if(result.next()){
                fail("More then 3 results (score)");
            }
            
            result = bet1x2.executeQuery();
            if(result.next()){
                fail("Non epmty bet 1x2");
            }
            
            result = betah.executeQuery();
            if(result.next()){
                fail("Non epmty bet ah");
            }
            
            result = betbtts.executeQuery();
            for(int i = 0; i < 2; i++){
                if(!result.next()){
                    fail("Less then 2 saved bets btts");
                }
                assertEquals((Long)result.getLong("matchid"), match4.getId());
                if(result.getString("betcompany").equals(betbtts_Fortuna_Match4.betCompany)){
                    assertEquals(result.getString("yesbet"), betbtts_Fortuna_Match4.yes.toPlainString());
                    assertEquals(result.getString("nobet"), betbtts_Fortuna_Match4.no.toPlainString());
                } else if(result.getString("betcompany").equals(betbtts_Bet365_Match4.betCompany)){
                    assertEquals(result.getString("yesbet"), betbtts_Bet365_Match4.yes.toPlainString());
                    assertEquals(result.getString("nobet"), betbtts_Bet365_Match4.no.toPlainString());
                } else {
                    fail("Bad bet company");
                }
            }
            if(result.next()){
                fail("More then 2 results");
            }
            
            result = betdc.executeQuery();
            if(result.next()){
                fail("Non epmty bet dc");
            }
            
            result = betdnb.executeQuery();
            if(result.next()){
                fail("Non epmty bet dnb");
            }
            
            result = betou.executeQuery();
            if(result.next()){
                fail("Non epmty bet ou");
            }
        } catch (SQLException ex){
            fail("Error in SQL statements: " + ex);
        }
    }
    
    @Test
    @SuppressWarnings("null")
    public void saveLoadAllTest(){
        Collection<Match> result = testingManager.loadAllMatches();
        if(result == null){
            fail("Null result");
        }
        if(!result.isEmpty()){
            fail("Empty DB and nonepmty result");
        }
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        result = testingManager.loadAllMatches();
        Collection<Match> expected = new LinkedList<>();
        expected.add(match1);
        expected.add(match4);
        expected.add(match3);
        expected.add(match2);
        assertEqualsCollectionsMatches(result, expected);
    }
    
    /**
     * Vymaže všetky kolekcie.
     * @param col 
     */
    private void clearThem(LoadProperties prop, Collection... col){
        prop.clear();
        for(Collection c : col){
            c.clear();
        }
    }
    
    @Test
    public void saveLoadWithPropertiesTest(){
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        
        Collection<Match> expectedMatches = new LinkedList<>();
        LoadProperties properties = new LoadProperties();
        
        zeroProperties(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        oneProperty(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        twoProperties(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        threeProperties(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        fourProperties(expectedMatches, properties);
    }
    
    /**
     * Kontrola načítania pri nezadanej žiadnej property.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void zeroProperties(Collection<Match> expectedMatches,
            LoadProperties properties){
        expectedMatches.add(match1);
        expectedMatches.add(match2);
        expectedMatches.add(match3);
        expectedMatches.add(match4);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Kontrola načítania pri zadanej jednej property.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void oneProperty(Collection<Match> expectedMatches,
            LoadProperties properties){
        startDate(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        endDate(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        betCompany(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        league(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        classes(expectedMatches, properties);
    }
    
    /**
     * Zadaný počiatočný dátum.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void startDate(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addStartDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        expectedMatches.add(match2);
        expectedMatches.add(match3);
        expectedMatches.add(match4);
        checkLoading(expectedMatches, properties);
        expectedMatches.clear();
        properties.addStartDate(new GregorianCalendar(2014, Calendar.JANUARY, 26));
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaný koncový dátum.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void endDate(Collection<Match> expectedMatches,
            LoadProperties properties){
        expectedMatches.add(match1);
        expectedMatches.add(match2);
        properties.addEndDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        checkLoading(expectedMatches, properties);
        expectedMatches.clear();
        properties.addEndDate(new GregorianCalendar(2013, Calendar.OCTOBER, 31));
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaná stávková spoločnosť.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void betCompany(Collection<Match> expectedMatches,
            LoadProperties properties){
        Match m1 = getMatchCopyWithoutBets(match1);
        Match m2 = getMatchCopyWithoutBets(match2);
        Match m3 = getMatchCopyWithoutBets(match3);
        
        m1.addBet(bet1x2_Tipsport_Match1);
        m1.addBet(betou_Tipsport_1p0_Match1);
        m1.addBet(betou_Tipsport_1p5_Match1);
        
        m2.addBet(bet1x2_Tipsport_Match2);
        
        m3.addBet(bet1x2_Tipsport_Match3);
        m3.addBet(betdc_Tipsport_Match3);
        m3.addBet(betdnb_Tipsport_Match3);
        
        expectedMatches.add(m1);
        expectedMatches.add(m2);
        expectedMatches.add(m3);
        properties.addBetCompany("tipsport");
        checkLoading(expectedMatches, properties);
        
        m1.addBet(betah_Doublebet_Plus0p75_Match1);
        m1.addBet(betah_Doublebet_Plus1p5_Match1);
        
        properties.addBetCompany("doublebet");
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaná liga.
     * @param expectedMatches
     * @param properties 
     */
    private void league(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addLeague("Česká republika", "1. liga");
        expectedMatches.add(match3);
        checkLoading(expectedMatches, properties);
        properties.addLeague("Slovensko", "Extraliga");
        expectedMatches.add(match2);
        checkLoading(expectedMatches, properties);
        
        clearThem(properties, expectedMatches);
        
        Match match = new Match(Sport.getSport(Sport.HANDBALL_STRING));
        match.addPartialScore(14, 15);
        match.addPartialScore(15, 16);
        MatchProperties prop = new MatchProperties();
        prop.setCountry("Slovensko");
        prop.setLeague("1. liga");
        prop.setDate(new GregorianCalendar(2012, Calendar.DECEMBER, 12));
        match.setProperties(prop);
        match.addBet(new Bet1x2("my", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        testingManager.saveMatch(match);
        
        properties.addLeague("Slovensko", "1. liga");
        expectedMatches.add(match);
        
        checkLoading(expectedMatches, properties);
        testingManager.deleteMatch(match);
        
        clearThem(properties, expectedMatches);
        
        properties.addLeague("Slovensko", "");
        expectedMatches.add(match1);
        expectedMatches.add(match2);
        checkLoading(expectedMatches, properties);
        
        clearThem(properties, expectedMatches);
        
        properties.addLeague(null, "1. liga");
        expectedMatches.add(match3);
        expectedMatches.add(match);
        match.setId(null);
        testingManager.saveMatch(match);
        checkLoading(expectedMatches, properties);
        testingManager.deleteMatch(match);
    }
    
    /**
     * Zadané požadované typy stávok.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void classes(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addBetClass(Bet1x2.class);
        properties.addBetClass(BetAsianHandicap.class);
        Match m1 = getMatchCopyWithoutBets(match1);
        Match m3 = getMatchCopyWithoutBets(match3);
        m1.addBet(bet1x2_Bet365_Match1);
        m1.addBet(bet1x2_Fortuna_Match1);
        m1.addBet(bet1x2_Tipsport_Match1);
        m1.addBet(betah_Bet365_Minus0p5_Match1);
        m1.addBet(betah_Bet365_Minus0p75_Match1);
        m1.addBet(betah_Doublebet_Plus0p75_Match1);
        m1.addBet(betah_Doublebet_Plus1p5_Match1);
        m1.addBet(betah_Fortuna_Minus0p5_Match1);
        m1.addBet(betah_Fortuna_Minus0p75_Match1);
        
        m3.addBet(bet1x2_Tipsport_Match3);
        m3.addBet(betah_Bet365_Plus1p0_Match3);
        
        expectedMatches.add(m1);
        expectedMatches.add(m3);
        expectedMatches.add(match2);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Kontrola načítania pri zadaných dvoch properties.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void twoProperties(Collection<Match> expectedMatches,
            LoadProperties properties){
        startDateEndDate(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        startDateBetCompany(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        startDateLeague(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        endDateBetCompany(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        endDateLeague(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        leagueBetCompany(expectedMatches, properties);
    }
    
    /**
     * Zadaný počiatočný a koncový dátum.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void startDateEndDate(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addStartDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        properties.addEndDate(new GregorianCalendar(2013, Calendar.DECEMBER, 31));
        expectedMatches.add(match2);
        expectedMatches.add(match3);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zdaraný počiatočný dtáum a stávková spoločnosť.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void startDateBetCompany(Collection<Match> expectedMatches,
            LoadProperties properties){
        Match m2 = getMatchCopyWithoutBets(match2);
        m2.addBet(bet1x2_Tipsport_Match2);
        
        Match m3 = getMatchCopyWithoutBets(match3);
        m3.addBet(bet1x2_Tipsport_Match3);
        m3.addBet(betdc_Tipsport_Match3);
        m3.addBet(betdnb_Tipsport_Match3);
        expectedMatches.add(m3);
        expectedMatches.add(m2);
        properties.addBetCompany("tipsport");
        properties.addStartDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaný počiatočný dátum a liga.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void startDateLeague(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addLeague("USA", "2. league");
        properties.addLeague("Česká republika", "1. liga");
        expectedMatches.add(match4);
        expectedMatches.add(match3);
        properties.addStartDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zdaraný koncový dátum a stávková spoločnosť
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void endDateBetCompany(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addBetCompany("tipsport");
        properties.addBetCompany("fortuna");
        properties.addBetCompany("bet365");
        Match m1 = getMatchCopyWithoutBets(match1);
        m1.addBet(bet1x2_Tipsport_Match1);
        m1.addBet(bet1x2_Fortuna_Match1);
        m1.addBet(bet1x2_Bet365_Match1);
        m1.addBet(betah_Bet365_Minus0p5_Match1);
        m1.addBet(betah_Bet365_Minus0p75_Match1);
        m1.addBet(betah_Fortuna_Minus0p5_Match1);
        m1.addBet(betah_Fortuna_Minus0p75_Match1);
        m1.addBet(betdc_Bet365_Match1);
        m1.addBet(betou_Tipsport_1p0_Match1);
        m1.addBet(betou_Tipsport_1p5_Match1);
        m1.addBet(betou_Fortuna_1p5_Match1);
        expectedMatches.add(m1);
        expectedMatches.add(match2);
        expectedMatches.add(match3);
        properties.addEndDate(new GregorianCalendar(2013, Calendar.DECEMBER, 31));
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaný koncový dátum a liga.
     * 
     * @param expectedMatches
     * @param properties 
     */
    private void endDateLeague(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addLeague("Slovensko", "Corgoň liga");
        properties.addLeague("Česká republika", "1. liga");
        expectedMatches.add(match1);
        expectedMatches.add(match3);
        properties.addEndDate(new GregorianCalendar(2013, Calendar.DECEMBER, 31));
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaná liga a stávková spoločnosť.
     * @param expectedMatches
     * @param properties 
     */
    private void leagueBetCompany(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addBetCompany("doublebet");
        properties.addBetCompany("tipsport");
        properties.addLeague("Slovensko", "Corgoň liga");
        properties.addLeague("USA", "2. league");
        Match m1 = getMatchCopyWithoutBets(match1);
        m1.addBet(bet1x2_Tipsport_Match1);
        m1.addBet(betou_Tipsport_1p0_Match1);
        m1.addBet(betou_Tipsport_1p5_Match1);
        m1.addBet(betah_Doublebet_Plus0p75_Match1);
        m1.addBet(betah_Doublebet_Plus1p5_Match1);
        expectedMatches.add(m1);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Kontrola pri zadaných 3 properties.
     * @param expectedMatches
     * @param properties 
     */
    private void threeProperties(Collection<Match> expectedMatches,
            LoadProperties properties){
        startDateEndDateLeague(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        startDateEndDateBetCompany(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        startDateLeagueBetCompany(expectedMatches, properties);
        clearThem(properties, expectedMatches);
        
        endDateLeagueBetCompany(expectedMatches, properties);
    }
    
    /**
     * Zadarný počiatočný a koncový dátum a liga.
     * @param expectedMatches
     * @param properties 
     */
    private void startDateEndDateLeague(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addLeague("Slovensko", "Extraliga");
        properties.addStartDate(new GregorianCalendar(2013, Calendar.OCTOBER, 31));
        properties.addEndDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        expectedMatches.add(match2);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaný počiatočný dátum, koncový dátum a stávková spoločnosť.
     * @param expectedMatches
     * @param properties 
     */
    private void startDateEndDateBetCompany(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addBetCompany("bet365");
        Match m1 = getMatchCopyWithoutBets(match1);
        Match m2 = getMatchCopyWithoutBets(match2);
        m1.addBet(bet1x2_Bet365_Match1);
        m1.addBet(betah_Bet365_Minus0p5_Match1);
        m1.addBet(betah_Bet365_Minus0p75_Match1);
        m1.addBet(betdc_Bet365_Match1);

        m2.addBet(bet1x2_Bet365_Match2);
        m2.addBet(betah_Bet365_Plus0p75_Match2);
        m2.addBet(betah_Bet365_Plus1p0_Match2);

        expectedMatches.add(m1);
        expectedMatches.add(m2);
        properties.addStartDate(new GregorianCalendar(2013, Calendar.OCTOBER, 31));
        properties.addEndDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaný počiatočný dátum, liga, stávková spoločnosť.
     * @param expectedMatches
     * @param properties 
     */
    private void startDateLeagueBetCompany(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addStartDate(new GregorianCalendar(2013, Calendar.DECEMBER, 21));
        properties.addLeague("Česká republika", "1. liga");
        properties.addLeague("USA", "2. league");
        properties.addBetCompany("fortuna");
        properties.addBetCompany("bet365");
        Match m3 = getMatchCopyWithoutBets(match3);
        m3.addBet(betah_Bet365_Plus1p0_Match3);
        m3.addBet(betbtts_Fortuna_Match3);
        m3.addBet(betou_Bet365_1p5_Match3);
        expectedMatches.add(match4);
        expectedMatches.add(m3);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Zadaný konečný dátum, liga a stávková spoločnosť.
     * @param expectedMatches
     * @param properties 
     */
    private void endDateLeagueBetCompany(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addLeague("Slovensko", "Extraliga");
        properties.addLeague("Slovensko", "Corgoň liga");
        properties.addBetCompany("fortuna");
        properties.addBetCompany("bet365");
        properties.addEndDate(new GregorianCalendar(2014, Calendar.JANUARY, 2));
        Match m1 = getMatchCopyWithoutBets(match1);
        m1.addBet(bet1x2_Fortuna_Match1);
        m1.addBet(bet1x2_Bet365_Match1);
        m1.addBet(betah_Bet365_Minus0p5_Match1);
        m1.addBet(betah_Bet365_Minus0p75_Match1);
        m1.addBet(betah_Fortuna_Minus0p5_Match1);
        m1.addBet(betah_Fortuna_Minus0p75_Match1);
        m1.addBet(betdc_Bet365_Match1);
        m1.addBet(betou_Fortuna_1p5_Match1);
        
        Match m2 = getMatchCopyWithoutBets(match2);
        m2.addBet(bet1x2_Bet365_Match2);
        m2.addBet(bet1x2_Fortuna_Match2);
        m2.addBet(betah_Bet365_Plus0p75_Match2);
        m2.addBet(betah_Bet365_Plus1p0_Match2);
        expectedMatches.add(m1);
        expectedMatches.add(m2);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Kontorla pri zadaných 4 LoadProperties.
     * @param expectedMatches
     * @param properties 
     */
    private void fourProperties(Collection<Match> expectedMatches,
            LoadProperties properties){
        properties.addStartDate(new GregorianCalendar(2013, Calendar.NOVEMBER, 15));
        properties.addEndDate(new GregorianCalendar(2014, Calendar.JANUARY, 1));
        properties.addLeague("Slovensko", "Extraliga");
        properties.addBetCompany("tipsport");
        properties.addBetCompany("bet365");
        Match m2 = getMatchCopyWithoutBets(match2);
        m2.addBet(bet1x2_Tipsport_Match2);
        m2.addBet(bet1x2_Bet365_Match2);
        m2.addBet(betah_Bet365_Plus0p75_Match2);
        m2.addBet(betah_Bet365_Plus1p0_Match2);
        expectedMatches.add(m2);
        checkLoading(expectedMatches, properties);
    }
    
    /**
     * Skontorluje, či testovací objekt vráti rovnakú kolekciu ako expectedMatches
     * pri zadaných properties.
     * 
     * @param expetedMatches
     * @param properties 
     */
    private void checkLoading(Collection<Match> expetedMatches, LoadProperties properties){
        assertEqualsCollectionsMatches(testingManager.loadMatches(properties),
                expetedMatches);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saveLoadWithPropertiesTestNull(){
        testingManager.loadMatches(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saveDeleteTestNull(){
        testingManager.deleteMatch(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saveDeleteTestNullID(){
        testingManager.deleteMatch(new Match(null));
    }
    
    @Test
    public void saveDeleteTest(){
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        try(PreparedStatement match = connection.prepareStatement("SELECT * FROM matches");
                PreparedStatement score = connection.prepareStatement("SELECT * FROM scores");
                PreparedStatement bet1x2 = connection.prepareStatement("SELECT * FROM bet1x2");
                PreparedStatement betah = connection.prepareStatement("SELECT * FROM betah");
                PreparedStatement betbtts = connection.prepareStatement("SELECT * FROM betbtts");
                PreparedStatement betdc = connection.prepareStatement("SELECT * FROM betdc");
                PreparedStatement betdnb = connection.prepareStatement("SELECT * FROM betdnb");
                PreparedStatement betou = connection.prepareStatement("SELECT * FROM betou")){
            hasNumberOfRows(match.executeQuery(), 2, "match", match4.getId(), match3.getId());
            hasNumberOfRows(score.executeQuery(), 8, "score", match4.getId(), match3.getId());
            hasNumberOfRows(bet1x2.executeQuery(), 1, "bet1x2", match4.getId(), match3.getId());
            hasNumberOfRows(betah.executeQuery(), 1, "betah", match4.getId(), match3.getId());
            hasNumberOfRows(betbtts.executeQuery(), 3, "betbtts", match4.getId(), match3.getId());
            hasNumberOfRows(betdc.executeQuery(), 1, "betdc", match4.getId(), match3.getId());
            hasNumberOfRows(betdnb.executeQuery(), 1, "betdnb", match4.getId(), match3.getId());
            hasNumberOfRows(betou.executeQuery(), 1, "betou", match4.getId(), match3.getId());
        } catch (SQLException ex){
            fail("Error in SQL statements: " + ex);
        }
        testingManager.deleteMatch(match3);
        try(PreparedStatement match = connection.prepareStatement("SELECT * FROM matches");
                PreparedStatement score = connection.prepareStatement("SELECT * FROM scores");
                PreparedStatement bet1x2 = connection.prepareStatement("SELECT * FROM bet1x2");
                PreparedStatement betah = connection.prepareStatement("SELECT * FROM betah");
                PreparedStatement betbtts = connection.prepareStatement("SELECT * FROM betbtts");
                PreparedStatement betdc = connection.prepareStatement("SELECT * FROM betdc");
                PreparedStatement betdnb = connection.prepareStatement("SELECT * FROM betdnb");
                PreparedStatement betou = connection.prepareStatement("SELECT * FROM betou")){
            hasNumberOfRows(match.executeQuery(), 1, "match", match4.getId());
            hasNumberOfRows(score.executeQuery(), 3, "score", match4.getId());
            hasNumberOfRows(bet1x2.executeQuery(), 0, "bet1x2", match4.getId());
            hasNumberOfRows(betah.executeQuery(), 0, "betah", match4.getId());
            hasNumberOfRows(betbtts.executeQuery(), 2, "betbtts", match4.getId());
            hasNumberOfRows(betdc.executeQuery(), 0, "betdc", match4.getId());
            hasNumberOfRows(betdnb.executeQuery(), 0, "betdnb", match4.getId());
            hasNumberOfRows(betou.executeQuery(), 0, "betou", match4.getId());
        } catch (SQLException ex){
            fail("Error in SQL statements: " + ex);
        }
        match3.setId(null);
        testingManager.saveMatch(match3);
        testingManager.deleteMatch(match4);
        try(PreparedStatement match = connection.prepareStatement("SELECT * FROM matches");
                PreparedStatement score = connection.prepareStatement("SELECT * FROM scores");
                PreparedStatement bet1x2 = connection.prepareStatement("SELECT * FROM bet1x2");
                PreparedStatement betah = connection.prepareStatement("SELECT * FROM betah");
                PreparedStatement betbtts = connection.prepareStatement("SELECT * FROM betbtts");
                PreparedStatement betdc = connection.prepareStatement("SELECT * FROM betdc");
                PreparedStatement betdnb = connection.prepareStatement("SELECT * FROM betdnb");
                PreparedStatement betou = connection.prepareStatement("SELECT * FROM betou")){
            hasNumberOfRows(match.executeQuery(), 1, "match", match3.getId());
            hasNumberOfRows(score.executeQuery(), 5, "score", match3.getId());
            hasNumberOfRows(bet1x2.executeQuery(), 1, "bet1x2", match3.getId());
            hasNumberOfRows(betah.executeQuery(), 1, "betah", match3.getId());
            hasNumberOfRows(betbtts.executeQuery(), 1, "betbtts", match3.getId());
            hasNumberOfRows(betdc.executeQuery(), 1, "betdc", match3.getId());
            hasNumberOfRows(betdnb.executeQuery(), 1, "betdnb", match3.getId());
            hasNumberOfRows(betou.executeQuery(), 1, "betou", match3.getId());
        } catch (SQLException ex){
            fail("Error in SQL statements: " + ex);
        }
    }
    
    @Test
    @SuppressWarnings("null")
    public void saveGetAvailableCountriesAndLeaguesTest(){
        Map<String, Collection<String>> ctrLgs = testingManager.getAvailableCountriesAndLeagues();
        if(ctrLgs == null){
            fail("Empty DB and null result.");
        }
        if(!ctrLgs.isEmpty()){
            fail("Empty DB and nonempty result.");
        }
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        ctrLgs = testingManager.getAvailableCountriesAndLeagues();
        Map<String, Collection<String>> req = new HashMap<>();
        req.put(match1.getProperties().getCountry(), 
                Arrays.asList(new String[]{
                        match1.getProperties().getLeague(),
                        match2.getProperties().getLeague()}));
        req.put(match3.getProperties().getCountry(), 
                Arrays.asList(new String[]{match3.getProperties().getLeague()}));
        checkMapEquality(ctrLgs, req);
        testingManager.saveMatch(match4);
        testingManager.deleteMatch(match1);
        
        ctrLgs = testingManager.getAvailableCountriesAndLeagues();
        req.clear();
        req.put(match2.getProperties().getCountry(), 
                Arrays.asList(new String[]{match2.getProperties().getLeague()}));
        req.put(match3.getProperties().getCountry(), 
                Arrays.asList(new String[]{match3.getProperties().getLeague()}));
        req.put(match4.getProperties().getCountry(), 
                Arrays.asList(new String[]{match4.getProperties().getLeague()}));
        checkMapEquality(ctrLgs, req);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saveGetAvialableCompaniesNullArgumentTest(){
        testingManager.getAvailableBetCompanies(null);
    }
    
    @Test
    public void saveGetAvialableCompaniesTest(){
        Collection<String> companies = testingManager.getAvailableBetCompanies(Bet.class);
        if(companies == null){
            fail("Returned collection is null");
        }
        companies = testingManager.getAvailableBetCompanies(Bet1x2.class);
        if(!companies.isEmpty()){
            fail("Epmty DB and nonepmty result.");
        }
        testingManager.saveMatch(match4);
        availableCompaniesShouldBeEmpty(Bet1x2.class, BetAsianHandicap.class,
                BetDoubleChance.class, BetDoubleChance.class, BetOverUnder.class);
        ensureEquality(BetBothTeamsToScore.class, "fortuna", "bet365");
        testingManager.saveMatch(match2);
        ensureEquality(BetBothTeamsToScore.class, "fortuna", "bet365");
        ensureEquality(Bet1x2.class, "fortuna", "bet365", "tipsport");
        ensureEquality(BetAsianHandicap.class, "bet365");
    }
    
    @Test
    public void saveGetFristDateTest(){
        isToday(testingManager.getFirstDate());
        testingManager.saveMatch(match3);
        assertEqualsCalendars(match3.getProperties().getDate(), testingManager.getFirstDate());
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        assertEqualsCalendars(match1.getProperties().getDate(), testingManager.getFirstDate());
        testingManager.deleteMatch(match1);
        assertEqualsCalendars(match2.getProperties().getDate(), testingManager.getFirstDate());
    }
    
    @Test
    public void saveGetLastDateTest(){
        isToday(testingManager.getLastDate());
        testingManager.saveMatch(match1);
        assertEqualsCalendars(match1.getProperties().getDate(), testingManager.getLastDate());
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match2);
        assertEqualsCalendars(match3.getProperties().getDate(), testingManager.getLastDate());
        testingManager.deleteMatch(match3);
        assertEqualsCalendars(match2.getProperties().getDate(), testingManager.getLastDate());
    }
    
    @Test
    public void saveDeleteBetCompanyTest(){
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        testingManager.deleteBetCompany("fortuna");
        Match m1 = getMatchCopyWithoutBets(match1);
        Match m2 = getMatchCopyWithoutBets(match2);
        Match m3 = getMatchCopyWithoutBets(match3);
        Match m4 = getMatchCopyWithoutBets(match4);
        m1.addBet(bet1x2_Tipsport_Match1);
        m1.addBet(bet1x2_Bet365_Match1);
        m1.addBet(betah_Bet365_Minus0p5_Match1);
        m1.addBet(betah_Bet365_Minus0p75_Match1);
        m1.addBet(betah_Doublebet_Plus1p5_Match1);
        m1.addBet(betah_Doublebet_Plus0p75_Match1);
        m1.addBet(betdc_Bet365_Match1);
        m1.addBet(betou_Tipsport_1p0_Match1);
        m1.addBet(betou_Tipsport_1p5_Match1);
        
        m2.addBet(bet1x2_Tipsport_Match2);
        m2.addBet(bet1x2_Bet365_Match2);
        m2.addBet(betah_Bet365_Plus0p75_Match2);
        m2.addBet(betah_Bet365_Plus1p0_Match2);
        
        m3.addBet(bet1x2_Tipsport_Match3);
        m3.addBet(betdc_Tipsport_Match3);
        m3.addBet(betdnb_Tipsport_Match3);
        m3.addBet(betah_Bet365_Plus1p0_Match3);
        m3.addBet(betou_Bet365_1p5_Match3);
        
        m4.addBet(betbtts_Bet365_Match4);
        Collection<Match> exp = new LinkedList<>();
        exp.add(m1);
        exp.add(m2);
        exp.add(m3);
        exp.add(m4);
        assertEqualsCollectionsMatches(exp, testingManager.loadAllMatches());
        
        //match1
        shouldBeEmptyResultSet("SELECT * FROM bet1x2 WHERE matchid = " + match1.getId() 
                + " AND betCompany LIKE 'fortuna'");
        shouldBeEmptyResultSet("SELECT * FROM betah WHERE matchid = " + match1.getId() 
                + " AND betCompany LIKE 'fortuna'");
        shouldBeEmptyResultSet("SELECT * FROM betou WHERE matchid = " + match1.getId() 
                + " AND betCompany LIKE 'fortuna'");
        
        //match2
        shouldBeEmptyResultSet("SELECT * FROM bet1x2 WHERE matchid = " + match2.getId() 
                + " AND betCompany LIKE 'fortuna'");
        //match3
        shouldBeEmptyResultSet("SELECT * FROM betbtts WHERE matchid = " + match3.getId() 
                + " AND betCompany LIKE 'fortuna'");
        //match4
        shouldBeEmptyResultSet("SELECT * FROM betbtts WHERE matchid = " + match4.getId() 
                + " AND betCompany LIKE 'fortuna'");
        
        
        testingManager.deleteBetCompany("bet365");
        m1 = getMatchCopyWithoutBets(m1);
        m2 = getMatchCopyWithoutBets(m2);
        m3 = getMatchCopyWithoutBets(m3);
        
        m1.addBet(bet1x2_Tipsport_Match1);
        m1.addBet(betah_Doublebet_Plus1p5_Match1);
        m1.addBet(betah_Doublebet_Plus0p75_Match1);
        m1.addBet(betou_Tipsport_1p0_Match1);
        m1.addBet(betou_Tipsport_1p5_Match1);
        
        m2.addBet(bet1x2_Tipsport_Match2);
        
        m3.addBet(bet1x2_Tipsport_Match3);
        m3.addBet(betdc_Tipsport_Match3);
        m3.addBet(betdnb_Tipsport_Match3);
        exp.clear();
        exp.add(m1);
        exp.add(m2);
        exp.add(m3);
        assertEqualsCollectionsMatches(exp, testingManager.loadAllMatches());
        
        //match1
        shouldBeEmptyResultSet("SELECT * FROM bet1x2 WHERE matchid = " + match1.getId() 
                + " AND betCompany LIKE 'bet365'");
        shouldBeEmptyResultSet("SELECT * FROM betah WHERE matchid = " + match1.getId() 
                + " AND betCompany LIKE 'bet365'");
        shouldBeEmptyResultSet("SELECT * FROM betdc WHERE matchid = " + match1.getId() 
                + " AND betCompany LIKE 'bet365'");
        //match2
        shouldBeEmptyResultSet("SELECT * FROM bet1x2 WHERE matchid = " + match2.getId() 
                + " AND betCompany LIKE 'bet365'");
        shouldBeEmptyResultSet("SELECT * FROM betah WHERE matchid = " + match2.getId() 
                + " AND betCompany LIKE 'bet365'");
        //match3
        shouldBeEmptyResultSet("SELECT * FROM betou WHERE matchid = " + match3.getId() 
                + " AND betCompany LIKE 'bet365'");
        shouldBeEmptyResultSet("SELECT * FROM betah WHERE matchid = " + match3.getId() 
                + " AND betCompany LIKE 'bet365'");
        //match4
        shouldBeEmptyResultSet("SELECT * FROM betbtts WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM scores WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM matches WHERE id = " + match4.getId());
    }
    
    @Test
    public void saveDeleteLeagueTest(){
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        testingManager.deleteLeague("Slovensko", "Corgoň liga");
        Collection<Match> exp = new LinkedList<>();
        exp.add(match2);
        exp.add(match3);
        exp.add(match4);
        assertEqualsCollectionsMatches(exp, testingManager.loadAllMatches());
        shouldBeEmptyResultSet("SELECT * FROM bet1x2 WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betah WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betbtts WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betdc WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betdnb WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betou WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM scores WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM matches WHERE id = " + match1.getId());
    }
    
    /**
     * Skontroluje, či zadaný sql výraz vráti z DB prázdny výsledok.
     * 
     * @param sql sql dotaz
     */
    private void shouldBeEmptyResultSet(String sql){
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            if(ps.executeQuery().next()){
                fail("Nonempty result. (" + sql + ")");
            }
        } catch (SQLException ex){
            fail(ex.toString());
        }
    }
    
    @Test
    public void saveDeleteCountrytest(){
        try{
            testingManager.deleteCountry(null);
            fail("Null bet company and no exception");
        } catch (BetIOException ex){
            //ok
        } catch (Exception e){
            fail("Null bet company: Bad exception " + e);
        }
        
        try{
            testingManager.deleteCountry("");
            fail("Empty bet company and no exception");
        } catch (BetIOException ex){
            //ok
        } catch (Exception e){
            fail("Empty bet company: Bad exception " + e);
        }
        
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        assertEqualsCollectionsMatches(testingManager.loadAllMatches(),
                Arrays.asList(new Match[] {match1, match2, match3, match4}));
        
        testingManager.deleteCountry("Slovensko");
        assertEqualsCollectionsMatches(testingManager.loadAllMatches(),
                Arrays.asList(new Match[] {match3, match4}));
        
        testingManager.deleteCountry("USA");
        assertEqualsCollectionsMatches(testingManager.loadAllMatches(),
                Arrays.asList(new Match[] {match3}));
    }
    
    @Test
    public void saveDeleteSportTest(){
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        testingManager.deleteSport(Sport.getSport(BettingSports.Soccer));
        Collection<Match> exp = new LinkedList<>();
        exp.add(match2);
        exp.add(match3);
        assertEqualsCollectionsMatches(exp, testingManager.loadAllMatches());
        
        shouldBeEmptyResultSet("SELECT * FROM bet1x2 WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betah WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betbtts WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betdc WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betdnb WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM betou WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM scores WHERE matchid = " + match1.getId());
        shouldBeEmptyResultSet("SELECT * FROM matches WHERE id = " + match1.getId());
        
        shouldBeEmptyResultSet("SELECT * FROM bet1x2 WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM betah WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM betbtts WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM betdc WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM betdnb WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM betou WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM scores WHERE matchid = " + match4.getId());
        shouldBeEmptyResultSet("SELECT * FROM matches WHERE id = " + match4.getId());
    }
    
    @Test
    public void saveDeleteAllTest(){
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        assertEqualsCollectionsMatches(testingManager.loadAllMatches(),
                Arrays.asList(new Match[] {match1, match2, match3, match4}));
        testingManager.deleteAll();
        assertEqualsCollectionsMatches(testingManager.loadAllMatches(),
                Arrays.asList(new Match[] {}));
    }
    
    /**
     * Skontroluje, či sú dva dátumy rovnaké. Používa compareTo vc Calendar.
     * @param c1
     * @param c2 
     */
    private void assertEqualsCalendars(Calendar c1, Calendar c2){
        if(c1.compareTo(c2) != 0){
            fail("Calendars are not equals. " + System.lineSeparator() + c1 
                    + System.lineSeparator() + c2);
        }
    }
    
     /**
      * Skontorluje, či kalendár c má dnešný dátum. Kontorluje iba deň,
      * mesiac a rok.
      * 
      * @param c 
      */
    private void isToday(Calendar c) {
        Calendar today = new GregorianCalendar();
        if(!(c.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && c.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && c.get(Calendar.DATE) == today.get(Calendar.DATE))){
            fail("Calendar date is not today");
        }
    }
    
    /**
     * Skontorluje, či testovaný manažér pre triedu clazz vráti presne zoznam
     * spoločností companies.
     * @param <T>
     * @param clazz
     * @param companies 
     */
    private <T extends Bet> void ensureEquality(Class<T> clazz, String... companies){
        assertEqualsCollectionsString(testingManager.getAvailableBetCompanies(clazz),
                Arrays.asList(companies));
    }
    
    /**
     * Skontroluje, či manažér vráti prázdnu kolekciu pre daný typ stávky.
     * @param <T>
     * @param clazz 
     */
    @SuppressWarnings("unchecked")
    private <T extends Bet> void availableCompaniesShouldBeEmpty(Class... clazz){
        for(Class<T> c : clazz){
            if(!testingManager.getAvailableBetCompanies(c).isEmpty()){
                fail("No records for class " + clazz + "and nonepmty result.");
            }
        }
    }
    
    /**
     * Skontorluje, či sú mapy rovnaké.
     * @param map1
     * @param map2 
     */
    private void checkMapEquality(Map<String, Collection<String>> map1,
            Map<String, Collection<String>> map2){
        if(assertAreBothNull(map1, map2)){
            return;
        }
        if(map1.size() != map2.size()){
            fail("Not same size.");
        }
        for(String key : map1.keySet()){
            assertEqualsCollectionsString(map1.get(key), map2.get(key));
        }
    }
    
    /**
     * Skontorluje dve kolekcie reťazcov.
     * @param col1
     * @param col2 
     */
    private void assertEqualsCollectionsString(Collection<String> col1, Collection<String> col2){
        if(collectionChecker(col1, col2)){
            List<String> l1 = new LinkedList<>(col1);
            List<String> l2 = new LinkedList<>(col2);
            Collections.sort(l1);
            Collections.sort(l2);
            for(int i = 0; i < l1.size(); i++){
                if(!l1.remove(0).equals(l2.remove(0))){
                    fail("Not same elemnts");
                }
            }
        }
    }
    
    /**
     * Skontroluje, či výsledok rs obsahuje požadovaný počet prvkov a či 
     * sú všetky z deklarovaných id v ids. Ak nie sú predané žiadne ids,
     * tak skontroluje iba počet prvkov.
     * 
     * @param rs výsledok
     * @param number požadovaný počet
     * @param items názov položiek
     * @param ids požadované id, ktoré majú byť prítomné. Nekontorluje ich počet
     *      vzhľadom ku number
     * @throws SQLException 
     */
    private void hasNumberOfRows(ResultSet rs, int number, String items, Long... ids)
            throws SQLException{
        for(int i = 0; i < number; i++){
            if(!rs.next()){
                fail("Some item missing (" + items + ")");
            }
            Long l = rs.getLong(1);
            boolean eq = false;
            for(Long id : ids){
                if(l.equals(id)){
                    eq = true;
                }
            }
            if(!eq && ids.length != 0){
                fail("Id not from declared ID`s");
            }
        }
        if(rs.next()){
            fail("Too much items(" + items + ")");
        }
    }

    /**
     * Zápasy sú porovnávané, ak majú rovnaké ID. Môže vyhodiť 
     * {@link java.lang.NullPointerException} ak niektoré id je {@code null}.
     * 
     * @param result
     * @param expected 
     */
    private void assertEqualsCollectionsMatches(Collection<Match> result, Collection<Match> expected) {
        if(!collectionChecker(result, expected)){
            return;
        }
        for(Match res : result){
            for(Match ex : expected){
                if(res.getId().equals(ex.getId())){
                    assertDeepEquals(res, ex);
                }
            }
        }
    }

    /**
     * Skontorluje, či sú zápasy rovnaké vrátane stávok.
     * @param res
     * @param ex 
     */
    private void assertDeepEquals(Match res, Match ex) {
        if(assertAreBothNull(ex, res)){
            return;
        }
        if(!res.equals(ex)){
            fail("Not equals matches " + res + "  " + ex);
        }
        Map<String, Collection<Bet>> betsRes = res.getBets();
        Map<String, Collection<Bet>> betsEx = ex.getBets();
        if(betsRes.size() != betsEx.size()){
            fail("Not same bets size {res: " + betsRes.size() + ", ex: " + betsEx.size() + "}"
                    + System.lineSeparator() + res.toString());
        }
        Collection<Bet> resColl, exColl;
        for(String bc : betsRes.keySet()){
            if(!betsEx.containsKey(bc)){
                fail("Bet company not in ex: " + bc);
            }
            resColl = betsRes.get(bc);
            exColl = betsEx.get(bc);
            assertEqualsCollectionsBets(resColl, exColl);
        }
    }

    /**
     * Skontorluje, či sú kolekcie stváok rovnaké. Resp, či ob
     * sahujú rovnaké prvky. Na poradí nezáleží.
     * 
     * @param resColl
     * @param exColl 
     */
    private void assertEqualsCollectionsBets(Collection<Bet> resColl, Collection<Bet> exColl) {
        if(!collectionChecker(resColl, exColl)){
            return;
        }
        LinkedList<Bet> resList = new LinkedList<>(resColl);
        LinkedList<Bet> exList = new LinkedList<>(exColl);
        Iterator<Bet> resIter = resList.iterator();
        Iterator<Bet> exIter;
        Bet resBet;
        boolean removed;
        while(resIter.hasNext()){
            exIter = exList.iterator();
            resBet = resIter.next();
            removed = false;
            while(exIter.hasNext() && !removed){
                if(exIter.next().equals(resBet)){
                    exIter.remove();
                    resIter.remove();
                    removed = true;
                }
            }
            if(!removed){
                fail("No bet found in ex for: " + resBet);
            }
        }
    }
    
    /**
     * Vráti kópiu zápasu bez jeho stávok.
     * @param match
     * @return 
     */
    private Match getMatchCopyWithoutBets(Match match){
        Match copy = new Match(match.getSport());
        copy.setId(match.getId());
        copy.setProperties(match.getProperties());
        copy.setScore(match.getScore());
        return copy;
    }
}