
package chorke.proprietary.bet.apps;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.sql.DataSource;
import org.postgresql.jdbc2.optional.PoolingDataSource;

/**
 *
 * Statické konštanty, enumerácie a iniciálne hodnoty pre aplikáciu.
 * @author Chorke
 */
public class StaticConstants {
    
    private StaticConstants(){}
    
    /**
     * Víťaz zápasu.
     */
    public static enum Winner {Team1, Team2, Tie};
    /**
     * Obdobie, pre sumarizáciu ziskov.
     */
    public static enum Periode {Day, Week, Month, Year};
    /**
     * Možnosť stávky.
     */
    public static enum BetPossibility {Home, Guest, Favorit, Looser, Tie};
    /**
     * Športy, na ktoré je možné staviť.
     */
    public static enum BettingSports {All, Soccer, Hockey, Basketball, Handball, Volleyball, Baseball};
    /**
     * Lacale pre Slovensko.
     */
    public static final Locale SVK_LOCALE = new Locale("sk", "SK");
    /**
     * Defaultný DataSource.
     */
    public static final DataSource DATA_SOURCE = initDataSource();
    /**
     * ResourceBundle s defaultným locale pre aplikáciu.
     */
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "chorke.proprietary.bet.apps.gui.properties.guistrings",
            getDefaultLocale());
    
    /**
     * Vráti defaultný Locale pre aplikáciu.
     * @return 
     */
    public static Locale getDefaultLocale(){
        return SVK_LOCALE;
    }
    
    /**
     * Inicializuje DataSource pre aplikáciu.
     * @return 
     */
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
