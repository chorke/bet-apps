
package chorke.bet.apps.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


/**
 * Interface, ktorý umožňuje zapisovať a čítať objekty zo streamov.
 * @author Chorke
 */
public interface IOTransferable {
    
    /**
     * Zapíše objekt pomocou stream.
     * @param stream
     * @throws IOException 
     */
    void save(BufferedWriter stream) throws IOException;
    
    /**
     * Načíta a nastaví jednotlivé hodnoty objektu zo stream.
     * @param stream
     * @throws IOException 
     */
    void load(BufferedReader stream) throws IOException;
}
