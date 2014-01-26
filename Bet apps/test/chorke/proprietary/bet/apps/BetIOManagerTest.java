
package chorke.proprietary.bet.apps;

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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Chorke
 */
public class BetIOManagerTest {
    
    private static Connection connection;
    private static BetIOManager testingManager;
    private Match match1, match2, match3, match4;
    
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
                "handicap varchar(5) NOT NULL," +
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
                "total varchar(5) NOT NULL," +
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
    }
    
    @Before
    public void initTest(){
        match1 = new Match(Sport.getSport(Sport.SOCCER_STRING));
        match2 = new Match(Sport.getSport(Sport.HOCKEY_STRING));
        match3 = new Match(Sport.getSport(Sport.BASKETBALL_STRING));
        match4 = new Match(Sport.getSport(Sport.SOCCER_STRING));
        
        MatchProperties prop1 = new MatchProperties();
        MatchProperties prop2 = new MatchProperties();
        MatchProperties prop3 = new MatchProperties();
        MatchProperties prop4 = new MatchProperties();
        
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
        
        match1.addBet(new Bet1x2("tipsport", 
                new BigDecimal("1.01"), new BigDecimal("4.87"), new BigDecimal("5.76")));
        match1.addBet(new Bet1x2("fortuna", 
                new BigDecimal("1.05"), new BigDecimal("4.88"), new BigDecimal("4.98")));
        match1.addBet(new Bet1x2("bet365", 
                new BigDecimal("1.03"), new BigDecimal("3.99"), new BigDecimal("4.90")));
        match1.addBet(new BetAsianHandicap("bet365", 
                new BigDecimal("1.03"), new BigDecimal("3.89"), new BigDecimal("-0.50"), ""));
        match1.addBet(new BetAsianHandicap("bet365", 
                new BigDecimal("1.10"), new BigDecimal("2.89"), new BigDecimal("-0.75"), "hadicap"));
        match1.addBet(new BetAsianHandicap("fortuna", 
                new BigDecimal("1.03"), new BigDecimal("3.99"), new BigDecimal("-0.50"), ""));
        match1.addBet(new BetAsianHandicap("fortuna", 
                new BigDecimal("1.10"), new BigDecimal("2.99"), new BigDecimal("-0.75"), ""));
        match1.addBet(new BetAsianHandicap("doublebet", 
                new BigDecimal("1.03"), new BigDecimal("3.43"), new BigDecimal("1.50"), "nohandicap"));
        match1.addBet(new BetAsianHandicap("doublebet", 
                new BigDecimal("1.10"), new BigDecimal("2.74"), new BigDecimal("0.75"), ""));
        match1.addBet(new BetDoubleChance("bet365", 
                new BigDecimal("1.13"), new BigDecimal("1.74"), new BigDecimal("1.75")));
        match1.addBet(new BetOverUnder("tipsport", 
                new BigDecimal("1.00"), new BigDecimal("2.74"), new BigDecimal("2.75"), ""));
        match1.addBet(new BetOverUnder("tipsport", 
                new BigDecimal("1.50"), new BigDecimal("2.74"), new BigDecimal("1.75"), ""));
        match1.addBet(new BetOverUnder("fortuna", 
                new BigDecimal("1.50"), new BigDecimal("2.75"), new BigDecimal("1.75"), ""));
        
        match2.addBet(new Bet1x2("tipsport", 
                new BigDecimal("1.10"), new BigDecimal("2.74"), new BigDecimal("1.75")));
        match2.addBet(new Bet1x2("bet365", 
                new BigDecimal("1.11"), new BigDecimal("2.70"), new BigDecimal("1.65")));
        match2.addBet(new Bet1x2("fortuna", 
                new BigDecimal("1.21"), new BigDecimal("2.70"), new BigDecimal("1.60")));
        match2.addBet(new BetAsianHandicap("bet365",
                new BigDecimal("1.10"), new BigDecimal("2.74"), new BigDecimal("0.75"), ""));
        match2.addBet(new BetAsianHandicap("bet365",
                new BigDecimal("1.56"), new BigDecimal("1.74"), new BigDecimal("1.00"), ""));
        
        match3.addBet(new Bet1x2("tipsport", 
                new BigDecimal("1.56"), new BigDecimal("1.74"), new BigDecimal("1.01")));
        match3.addBet(new BetDoubleChance("tipsport",
                new BigDecimal("1.56"), new BigDecimal("1.88"), new BigDecimal("1.97")));
        match3.addBet(new BetDrawNoBet("tipsport",
                new BigDecimal("1.88"), new BigDecimal("1.95")));
        match3.addBet(new BetBothTeamsToScore("fortuna",
                new BigDecimal("1.56"), new BigDecimal("1.97")));
        match3.addBet(new BetAsianHandicap("bet365", 
                new BigDecimal("1.56"), new BigDecimal("1.74"), new BigDecimal("1.00"), "nohan"));
        match3.addBet(new BetOverUnder("bet365", 
                new BigDecimal("1.50"), new BigDecimal("1.74"), new BigDecimal("1.65"), "yeshan"));
        
        match4.addBet(new BetBothTeamsToScore("fortuna",
                new BigDecimal("1.56"), new BigDecimal("1.74")));
        match4.addBet(new BetBothTeamsToScore("bet365",
                new BigDecimal("1.82"), new BigDecimal("1.67")));
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
    }
    
    @Test
    public void saveTest(){
        
    }
    
    @Test
    public void saveLoadAllTest(){
        
    }
    
    @Test
    public void saveLoadTest(){
        
    }
    
    @Test
    public void saveDeleteTest(){
        
    }
}