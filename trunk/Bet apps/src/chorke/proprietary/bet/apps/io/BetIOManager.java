
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.core.match.Match;
import java.util.Collection;

/**
 * Ukladá stávky.
 * @author Chorke
 */
public interface BetIOManager{
    
    /**
     * Uloží zápas do databázi. 
     * @param match
     * @throws BetIOException ak sa vyskytne problém pri ukladaní.
     */
    void saveMatch(Match match) throws BetIOException;
    
    /**
     * Načíta a vráti všetky zápasy z dtabázi
     * @return
     * @throws BetIOException ak sa vyskytne chyba pri čítaní zápasov z databázi
     */
    Collection<Match> loadAllMatches() throws BetIOException;
    
    /**
     * Načíta zápasy z databázi podľa zvolených {@link LoadProperties}.
     * @param properties
     * @return
     * @throws BetIOException ak sa vyskytne chyba pri čítanú zápasov z databázi
     * @see LoadProperties
     */
    Collection<Match> loadMatches(LoadProperties properties) throws BetIOException;
    
    /**
     * Odstráni zápas z databázi.
     * @param match
     * @throws BetIOException 
     */
    void deleteMatch(Match match) throws BetIOException;
}
