
package chorke.proprietary.bet.apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.sql.DataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.postgresql.ds.PGPoolingDataSource;

/**
 *
 * @author Chorke
 */
public class BetIOManagerTest {
    
    private static Connection con;
    
    @BeforeClass
    public static void initClass(){
        try{
            con = DriverManager.getConnection("jdbc:derby:memory:testDB;create=true");
            Statement st = con.createStatement();
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
            con = null;
        }
    }
    
    @AfterClass
    public static void tearDownClass() throws SQLException{
        try(Statement st = con.createStatement()){
            st.addBatch("DROP TABLE matches");
            st.addBatch("DROP TABLE scores");
            st.addBatch("DROP TABLE bet1x2");
            st.addBatch("DROP TABLE betah");
            st.addBatch("DROP TABLE betbtts");
            st.addBatch("DROP TABLE betdc");
            st.addBatch("DROP TABLE betdnb");
            st.addBatch("DROP TABLE betou");
            st.executeBatch();
            con.close();
            con = null;
        } catch (SQLException ex){
            System.out.println("AFTER CLASS: " + ex);
        }
    }
    
    @After
    public void tearDownTest(){
        try(Statement st = con.createStatement()){
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
    public void nothig(){
        System.out.println("doing nothing");
    }
}