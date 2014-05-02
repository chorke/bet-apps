
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.core.Tuple;
import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.core.match.Match;
import java.util.Collection;
import java.util.Map;

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
    
    /**
     * Vráti dostupné stávkové spoločnosti v DB pre konkrétny typ stávky. 
     * @param clazz trieda reprezentujúca požadovaný typ stávky
     * @return 
     * @throws BetIOException ak nastane nejaká chyba pri práci s DB
     * @throws IllegalArgumentException ak je clazz null
     */
    <T extends Bet> Collection<String> getAvailableBetCompanies(Class<T> clazz) 
            throws BetIOException, IllegalArgumentException;
    
    /**
     * Vráti mapu dostupných krajín a v nich dostupných líg. Vrátená mapa
     * má ako kľúč krajinu a hodnota pri každom kľúči sú dostupné ligy.
     * @return 
     * @throws BetIOException ak nastane nejaká chyba pri práci s DB
     */
    Map<String, Collection<String>> getAvailableCountriesAndLeagues() throws BetIOException;
}
