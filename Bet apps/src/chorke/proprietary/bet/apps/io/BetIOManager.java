
package chorke.proprietary.bet.apps.io;

import chorke.proprietary.bet.apps.core.bets.Bet;
import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.match.sports.Sport;
import java.util.Calendar;
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
     * @throws BetIOException ak sa vyskytne chyba pri čítaní zápasov z databázi
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
    
    /**
     * Vráti prvý dátum, ktorý sa vyskytuje v DB. Ak je DB prázdna, vráti
     * dnešný dátum.
     * 
     * @return
     * @throws BetIOException ak nastane nejaká chyba pri práci s DB
     */
    Calendar getFirstDate() throws BetIOException;
    
    /**
     * Vráti posledný dátum, ktorý sa vyskytuje v DB. Ak je DB prázdna, vráti
     * dnešný dátum.
     * 
     * @return
     * @throws BetIOException ak nastane nejaká chyba pri práci s DB
     */
    Calendar getLastDate() throws BetIOException;
    
    /**
     * Zastaví vykonávanie všetkých aktuálnych dotazov. Volaná metóda, ktorá 
     * aktuálne vykonáva nejaký SQL dotaz vyhodí novú výnimku BetIOException.
     */
    void cancelActualQuery();
    
    /**
     * Vymaže z DB všetky stávky spoločnosti {@code betCompany}. Ak ostane 
     * niektorý zápas bez stávok, vymaže ho tiež.
     * 
     * @param betCompany názov stávkovej spoločnosti
     * @throws BetIOException ak vznikne nejaká chyba počas mazania alebo
     *      je {@code betCompany == null} alebo prázdny reťazec
     */
    void deleteBetCompany(String betCompany) throws BetIOException;
    
    /**
     * Vymaže z DB všetky zápasy z ligy {@code league}, ktorá sa hrá v krajine
     * {@code country}.
     * 
     * @param country krajina, kde sa hrá liga
     * @param league názov ligy
     * @throws BetIOException ak vznikne nejaká chyba počas mazania alebo
     *      sú liga a kranjina {@code null} alebo prázdne reťazce
     */
    void deleteLeague(String country, String league) throws BetIOException;
    
    /**
     * Vymaže všetky ligy pre danú krajinu {@code country}.
     * 
     * @param country krajina, ktorá mý byť vymazaná
     * @throws BetIOException ak nastane nejaká chyba pri mazaní, aebo je 
     *      {@code country null} alebo prázdna
     */
    void deleteCountry(String country) throws BetIOException;
    
    /**
     * Vymaže z DB všetky zápasy z daného športu.
     * 
     * @param sport šport
     * @throws BetIOException ak vznikne nejaká chyba počas mazania alebo 
     *          je {@code sport == null}.
     */
    void deleteSport(Sport sport) throws BetIOException;
    
    /**
     * Vymaže všetky zápasy z DB vrátane ich stávok.
     * @throws BetIOException ak nastane nejaká chyba pri mazaní
     */
    void deleteAll() throws BetIOException;
}
