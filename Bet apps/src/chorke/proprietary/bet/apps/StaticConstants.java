
package chorke.proprietary.bet.apps;

import java.util.Locale;
import javax.sql.DataSource;
import org.postgresql.jdbc2.optional.PoolingDataSource;

/**
 *
 * Statické konštanty, enumerácie a iniciálne hodnoty pre aplikáciu.
 * @author Chorke
 */
public class StaticConstants {
    
    private StaticConstants(){}
    
    public static enum Winner {Team1, Team2, Tie};
    
    public static enum Periode {Day, Week, Month, Year};
    
    public static enum BetPossibility {Home, Guest, Favorit, Looser, Tie};
    
    public static final Locale SVK_LOCALE = new Locale("sk", "SK");
    
    public static final DataSource DATA_SOURCE = initDataSource();
    
    private static DataSource initDataSource(){
        PoolingDataSource outDs = new PoolingDataSource();
        outDs.setPortNumber(5432);
        outDs.setServerName("localhost");
        outDs.setUser("Juraj Durani");
        outDs.setPassword("288charlotte462");
        outDs.setDatabaseName("BetAppsDB");
        return outDs;
    }
}
