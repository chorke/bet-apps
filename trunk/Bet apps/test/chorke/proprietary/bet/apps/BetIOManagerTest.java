
package chorke.proprietary.bet.apps;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.core.bets.Bet1x2;
import chorke.proprietary.bet.apps.core.bets.BetAsianHandicap;
import chorke.proprietary.bet.apps.core.bets.BetBothTeamsToScore;
import chorke.proprietary.bet.apps.core.bets.BetDoubleChance;
import chorke.proprietary.bet.apps.core.bets.BetDrawNoBet;
import chorke.proprietary.bet.apps.core.bets.BetOverUnder;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.MatchProperties;
import chorke.proprietary.bet.apps.core.match.sports.Sport;
import chorke.proprietary.bet.apps.io.BetIOManager;
import chorke.proprietary.bet.apps.io.DBBetIOManager;
import chorke.proprietary.bet.apps.io.LoadProperties;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

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
                "bet1 varchar(5)," +
                "betx varchar(5)," +
                "bet2 varchar(5)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betah(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "bet1 varchar(5)," +
                "bet2 varchar(5)," +
                "handicap varchar(7) NOT NULL," +
                "description varchar(20) NOT NULL," +
                "PRIMARY KEY (matchid, betcompany, handicap, description))");
            st.addBatch("CREATE TABLE betbtts(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "yesbet varchar(5)," +
                "nobet varchar(5)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betdc(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "bet1x varchar(5)," +
                "bet2x varchar(5)," +
                "bet12 varchar(5)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betdnb(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "bet1 varchar(5)," +
                "bet2 varchar(5)," +
                "PRIMARY KEY (matchid, betcompany))");
            st.addBatch("CREATE TABLE betou(" +
                "matchid integer NOT NULL," +
                "betcompany varchar(50) NOT NULL," +
                "total varchar(6) NOT NULL," +
                "overbet varchar(5)," +
                "underbet varchar(5)," +
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
    
    @Test
    public void saveLoadWithPropertiesTest(){
        testingManager.saveMatch(match1);
        testingManager.saveMatch(match2);
        testingManager.saveMatch(match3);
        testingManager.saveMatch(match4);
        //0 properties
        Collection<Match> expetcedMatches = new LinkedList<>();
        expetcedMatches.add(match1);
        expetcedMatches.add(match2);
        expetcedMatches.add(match3);
        expetcedMatches.add(match4);
        loadWithProperties(expetcedMatches);
        //1 property
            //startDate
        expetcedMatches.clear();
        GregorianCalendar cal = new GregorianCalendar(2013, Calendar.NOVEMBER, 15);
        expetcedMatches.add(match2);
        expetcedMatches.add(match3);
        expetcedMatches.add(match4);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, cal));
        expetcedMatches.clear();
        cal = new GregorianCalendar(2014, Calendar.JANUARY, 26);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, cal));
            //endDate
        expetcedMatches.clear();
        cal = new GregorianCalendar(2013, Calendar.NOVEMBER, 15);
        expetcedMatches.add(match1);
        expetcedMatches.add(match2);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.END_DATE, cal));
        expetcedMatches.clear();
        cal = new GregorianCalendar(2013, Calendar.OCTOBER, 31);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.END_DATE, cal));
            //betCompany
        expetcedMatches.clear();
        
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
        
        expetcedMatches.add(m1);
        expetcedMatches.add(m2);
        expetcedMatches.add(m3);
        Collection<String> stringSet = new HashSet<>();
        stringSet.add("tipsport");
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.BET_COMPANY, stringSet));
        
        m1.addBet(betah_Doublebet_Plus0p75_Match1);
        m1.addBet(betah_Doublebet_Plus1p5_Match1);
        
        stringSet.add("doublebet");
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.BET_COMPANY, stringSet));
            //league
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("1. liga");
        expetcedMatches.add(match3);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.LEAGUE, stringSet));
        stringSet.add("Extraliga");
        expetcedMatches.add(match2);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.LEAGUE, stringSet));
        //2 properties
            //startDate, endDate
        GregorianCalendar start = new GregorianCalendar(2013, Calendar.NOVEMBER, 15);
        GregorianCalendar end = new GregorianCalendar(2013, Calendar.DECEMBER, 31);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, start),
                new Tuple(LoadProperties.END_DATE, end));
            //startDate, betCompany
        expetcedMatches.clear();
        m2 = getMatchCopyWithoutBets(match2);
        m2.addBet(bet1x2_Tipsport_Match2);
        
        m3 = getMatchCopyWithoutBets(match3);
        m3.addBet(bet1x2_Tipsport_Match3);
        m3.addBet(betdc_Tipsport_Match3);
        m3.addBet(betdnb_Tipsport_Match3);
        expetcedMatches.add(m3);
        expetcedMatches.add(m2);
        stringSet.clear();
        stringSet.add("tipsport");
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, start),
                new Tuple(LoadProperties.BET_COMPANY, stringSet));
            //startDate, league
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("2. league");
        stringSet.add("1. liga");
        expetcedMatches.add(match4);
        expetcedMatches.add(match3);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, start),
                new Tuple(LoadProperties.LEAGUE, stringSet));
            //endDate, betCompany
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("tipsport");
        stringSet.add("fortuna");
        stringSet.add("bet365");
        m1 = getMatchCopyWithoutBets(match1);
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
        expetcedMatches.add(m1);
        expetcedMatches.add(match2);
        expetcedMatches.add(match3);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.END_DATE, end),
                new Tuple(LoadProperties.BET_COMPANY, stringSet));
            //endDate, league
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("Corgoň liga");
        stringSet.add("1. liga");
        expetcedMatches.add(match1);
        expetcedMatches.add(match3);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.END_DATE, end),
                new Tuple(LoadProperties.LEAGUE, stringSet));
            //betCompany, league
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("doublebet");
        stringSet.add("tipsport");
        stringSet.add("Corgoň liga");
        stringSet.add("2. league");
        m1 = getMatchCopyWithoutBets(match1);
        m1.addBet(bet1x2_Tipsport_Match1);
        m1.addBet(betou_Tipsport_1p0_Match1);
        m1.addBet(betou_Tipsport_1p5_Match1);
        m1.addBet(betah_Doublebet_Plus0p75_Match1);
        m1.addBet(betah_Doublebet_Plus1p5_Match1);
        expetcedMatches.add(m1);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.BET_COMPANY, stringSet),
                new Tuple(LoadProperties.LEAGUE, stringSet));
        //3 properties
            // startDate, endDate, league
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("Extraliga");
        start = new GregorianCalendar(2013, Calendar.OCTOBER, 31);
        end = new GregorianCalendar(2013, Calendar.NOVEMBER, 15);
        expetcedMatches.add(match2);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, start),
                new Tuple(LoadProperties.END_DATE, end),
                new Tuple(LoadProperties.LEAGUE, stringSet));
            // startDate, endDate, betCompany
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("bet365");
        m1 = getMatchCopyWithoutBets(match1);
        m2 = getMatchCopyWithoutBets(match2);
        m1.addBet(bet1x2_Bet365_Match1);
        m1.addBet(betah_Bet365_Minus0p5_Match1);
        m1.addBet(betah_Bet365_Minus0p75_Match1);
        m1.addBet(betdc_Bet365_Match1);

        m2.addBet(bet1x2_Bet365_Match2);
        m2.addBet(betah_Bet365_Plus0p75_Match2);
        m2.addBet(betah_Bet365_Plus1p0_Match2);

        expetcedMatches.add(m1);
        expetcedMatches.add(m2);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, start),
                new Tuple(LoadProperties.END_DATE, end),
                new Tuple(LoadProperties.BET_COMPANY, stringSet));
            // startDate, league, betCompany
        expetcedMatches.clear();
        start = new GregorianCalendar(2013, Calendar.DECEMBER, 21);
        stringSet.clear();
        stringSet.add("1. liga");
        stringSet.add("2. league");
        stringSet.add("fortuna");
        stringSet.add("bet365");
        m3 = getMatchCopyWithoutBets(match3);
        m3.addBet(betah_Bet365_Plus1p0_Match3);
        m3.addBet(betbtts_Fortuna_Match3);
        m3.addBet(betou_Bet365_1p5_Match3);
        expetcedMatches.add(match4);
        expetcedMatches.add(m3);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, start),
                new Tuple(LoadProperties.LEAGUE, stringSet),
                new Tuple(LoadProperties.BET_COMPANY, stringSet));
            // endDate, league, betCompany
        expetcedMatches.clear();
        stringSet.clear();
        stringSet.add("Extraliga");
        stringSet.add("Corgoň liga");
        stringSet.add("fortuna");
        stringSet.add("bet365");
        end = new GregorianCalendar(2014, Calendar.JANUARY, 2);
        m1 = getMatchCopyWithoutBets(match1);
        m1.addBet(bet1x2_Fortuna_Match1);
        m1.addBet(bet1x2_Bet365_Match1);
        m1.addBet(betah_Bet365_Minus0p5_Match1);
        m1.addBet(betah_Bet365_Minus0p75_Match1);
        m1.addBet(betah_Fortuna_Minus0p5_Match1);
        m1.addBet(betah_Fortuna_Minus0p75_Match1);
        m1.addBet(betdc_Bet365_Match1);
        m1.addBet(betou_Fortuna_1p5_Match1);
        
        m2 = getMatchCopyWithoutBets(match2);
        m2.addBet(bet1x2_Bet365_Match2);
        m2.addBet(bet1x2_Fortuna_Match2);
        m2.addBet(betah_Bet365_Plus0p75_Match2);
        m2.addBet(betah_Bet365_Plus1p0_Match2);
        expetcedMatches.add(m1);
        expetcedMatches.add(m2);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.END_DATE, end),
                new Tuple(LoadProperties.LEAGUE, stringSet),
                new Tuple(LoadProperties.BET_COMPANY, stringSet));
        //4 properties
        expetcedMatches.clear();
        start = new GregorianCalendar(2013, Calendar.NOVEMBER, 15);
        end = new GregorianCalendar(2014, Calendar.JANUARY, 1);
        stringSet.clear();
        stringSet.add("Extraliga");
        stringSet.add("tipsport");
        stringSet.add("bet365");
        m2 = getMatchCopyWithoutBets(match2);
        m2.addBet(bet1x2_Tipsport_Match2);
        m2.addBet(bet1x2_Bet365_Match2);
        m2.addBet(betah_Bet365_Plus0p75_Match2);
        m2.addBet(betah_Bet365_Plus1p0_Match2);
        expetcedMatches.add(m2);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.START_DATE, start),
                new Tuple(LoadProperties.END_DATE, end),
                new Tuple(LoadProperties.LEAGUE, stringSet),
                new Tuple(LoadProperties.BET_COMPANY, stringSet));
        // bet class
        expetcedMatches.clear();
        HashSet<Class> classSet = new HashSet<>();
        classSet.add(Bet1x2.class);
        classSet.add(BetAsianHandicap.class);
        m1 = getMatchCopyWithoutBets(match1);
        m3 = getMatchCopyWithoutBets(match3);
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
        
        expetcedMatches.add(m1);
        expetcedMatches.add(m3);
        expetcedMatches.add(match2);
        loadWithProperties(expetcedMatches, new Tuple(LoadProperties.BET_CLASS, classSet));
    }
    
    private void loadWithProperties(Collection<Match> expetedMatches, Tuple... properties){
        LoadProperties lp = new LoadProperties();
        for(Tuple tp : properties){
            lp.put(tp.first, tp.second);
        }
        assertEqualsCollectionsMatches(testingManager.loadMatches(lp),
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
        if(result == null && expected == null){ return; }
        if(result == null && expected != null
                || result != null && expected == null){ fail("Only one is null"); }
        if(result.size() != expected.size()){ 
            System.out.println(result);
            System.out.println(expected);
            fail("Not same size (matches) {ex: " 
                + expected.size() + ", res: " + result.size() + "}"); }
        for(Match res : result){
            for(Match ex : expected){
                if(res.getId().equals(ex.getId())){
                    assertDeepEquals(res, ex);
                }
            }
        }
    }

    private void assertDeepEquals(Match res, Match ex) {
        if(res == null && ex == null){ return; }
        if(res != null){
            if(!res.equals(ex)){
                fail("Not equals matches " + res + "  " + ex);
            }
        } else {
            fail("res == null, ex != null");
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
                fail("Bet company not int ex: " + bc);
            }
            resColl = betsRes.get(bc);
            exColl = betsEx.get(bc);
            assertEqualsCollectionsBets(resColl, exColl);
        }
    }

    private void assertEqualsCollectionsBets(Collection<Bet> resColl, Collection<Bet> exColl) {
        if(resColl == null && exColl == null){ return; }
        if(resColl == null && exColl != null
                || resColl != null && exColl == null){ fail("Only one is null"); }
        if(resColl.size() != exColl.size()){ fail("Not same size (bets) {ex: " 
                + exColl.size() + ", res: " + resColl.size() + "}"); }
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
    
    private Match getMatchCopyWithoutBets(Match match){
        Match copy = new Match(match.getSport());
        copy.setId(match.getId());
        copy.setProperties(match.getProperties());
        copy.setScore(match.getScore());
        return copy;
    }
}