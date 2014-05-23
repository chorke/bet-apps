
package chorke.proprietary.bet.apps;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.sql.DataSource;
import org.postgresql.jdbc2.optional.PoolingDataSource;

/**
 * Statické konštanty, enumerácie a iniciálne hodnoty pre aplikáciu.
 * 
 * @author Chorke
 */
public class StaticConstants {
    
    /**
     * Privátny konštruktor.
     */
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
    public static enum BetPossibility {Home, Guest, Favorit, Loser, Tie};
    /**
     * Športy, na ktoré je možné staviť.
     */
    public static enum BettingSports {All, Soccer, Hockey, Basketball, Handball, Volleyball, Baseball};
    /**
     * Lacale pre Slovensko.
     */
    public static final Locale SVK_LOCALE = new Locale("sk", "SK");
    /**
     * Lacale pre US.
     */
    public static final Locale EN_US_LOCALE = new Locale("en", "US");
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
     * Systémovo závislý oddeľovač súborov.
     */
    public static final String FILE_SEP = System.getProperty("file.separator");
    
    /**
     * Vráti defaultný Locale pre aplikáciu.
     * @return 
     */
    public static Locale getDefaultLocale(){
        return SVK_LOCALE;
//        return EN_US_LOCALE;
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
//        outDs.setUser("Marek");
//        outDs.setPassword("8888");
        outDs.setDatabaseName("BetAppsDB");
        return outDs;
    }
    
    /**
     * Zložka s užívateľskými profilmi.
     */
    private static File usersDirectory;
    
    /**
     * Vráti zložku, ktorá obsahuje užívateľské profily.
     * 
     * @return zložku s užívateľskými profilmi.
     */
    public static File getUsersDirectory(){
        if(usersDirectory == null){
            try{
                usersDirectory = new File(
                                new File(StaticConstants.class
                                        .getProtectionDomain()
                                        .getCodeSource()
                                        .getLocation()
                                        .toURI())
                                    .getParentFile()
                                    .getParentFile(), "users");
                if(!usersDirectory.exists()){
                    usersDirectory.mkdirs();
                }
            } catch(URISyntaxException ex){
                //nemalo by sa stať
            }
        }
        return usersDirectory;
    }
    
    /**
     * Rozhodne, či dva dátumy označujú rovnaký deň alebo nie. Neberie ohľad
     * ona menšie časové jednotky ako dni. 
     * 
     * @param c1 dátum
     * @param c2 dátum
     * @return true, ak sp oba dátumy rovnaké, false inak
     */
    public static boolean areSameDates(Calendar c1, Calendar c2){
        return c1.get(Calendar.DATE) == c2.get(Calendar.DATE)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }
    
    /**
     * Vráti reťazec pre čas s vypočítanými sekundami a 
     * mili/mikrko/nano sekundami.
     * 
     * @param s reťazec, ktorý sa vloží pre výsledný čas
     * @param nanoTime čas vo forme trvania, nie aktuálny čas od počiatku PC času
     * @return reťazec s časom
     */
    public static String formatTime(String s, long nanoTime){
        long withoutNano = (nanoTime/1000);
        long nano = nanoTime - (withoutNano * 1000);
        long withoutMicro = withoutNano/1000;
        long micro = withoutNano - (withoutMicro * 1000);
        long withoutMili = withoutMicro/1000;
        long mili = withoutMicro - (withoutMili * 1000);
        return s + withoutMili + "s  " + mili + "ms  " + micro + "us  " + nano + "ns";
    }
}
