/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chorke.proprietary.bet.apps.core.httpparsing;

import chorke.proprietary.bet.apps.core.match.Match;
import chorke.proprietary.bet.apps.core.Tuple;
import java.util.Calendar;
import java.util.Collection;

/**
 * Stiahne a rozparsuje stávky z web stránky.
 * @author Chorke
 */
public interface HTMLBetParser {
    
    public static enum BettingSports {All, Soccer, Hockey, Basketball, Handball, Volleyball, Baseball};
    
    /**
     * Stiahne a vráti zápasy z dňa {@code cal}. Stiahnuté športy závisia na
     * aktuálne nastavenom {@link BettingSports} ({@link #setExploredSport(BettingSports)}).
     * @param cal
     * @return 
     * @see #setExploredSport(BettingSports) 
     */
    Collection<Match> getMatchesOfDay(Calendar cal);
    
    /**
     * Stiahne a vráti zápasy z predošlého dňa. Predošlý deň je relatívny 
     * vzhľadom ku počiatočnému dátumu ({@link #setStartDate(java.util.Calendar) }).
     * Stiahnuté športy závisia na aktuálne nastavenom {@link BettingSports}
     * ({@link #setExploredSport(BettingSports)}). Môže manipulovať aj z 
     * koncovým dátumom. Teda volanie tejto metódy môže zrušiť predošlé volanie
     * metódy {@link #setEndDate(java.util.Calendar)}.
     * @return 
     * @see #setStartDate(java.util.Calendar)
     * @see #setExploredSport(BettingSports)
     */
    Collection<Match> previousDay();
    
    /**
     * Stiahne a vráti zápasy z nasledujúceho dňa. Nasledujúci deň je relatívny 
     * vzhľadom ku koncovému dátumu ({@link #setEndDate(java.util.Calendar)}). 
     * Stiahnuté športy závisia na aktuálne nastavenom {@link BettingSports}
     * ({@link #setExploredSport(BettingSports)}). Môže manipulovať aj z počiatočným
     * dátumom. Teda volanie tejto metódy môže zrušiť predošlé volanie metódy
     * {@link #setStartDate(java.util.Calendar)}.
     * @return 
     * @see #setEndDate(java.util.Calendar) 
     * @see #setExploredSport(BettingSports)  
     */
    Collection<Match> nextDay();
    
    /**
     * Stiahne a vráti zápasy z obdobia podľa nastaveného štartovného a koncového
     * dátumu ({@link #setStartDate(Calendar)}, {@link #setEndDate(Calendar)}).
     * Stiahnuté športy závisia na aktuálne nastavenom {@link BettingSports}
     * ({@link #setExploredSport(BettingSports)}).
     * @return 
     * @see #setEndDate(java.util.Calendar) 
     * @see #setStartDate(java.util.Calendar) 
     * @see #setExploredSport(BettingSports)
     */
    Collection<Match> getMatches();
    
    /**
     * Nastaví šport, ktorý sa stiahne pri ďalšom požiadavku na stihanutie 
     * (ktorákoľvek z metód {@link #getMatches()}, {@link #getMatchesOfDay(Calendar)},
     * {@link #previousDay()}, {@link #nextDay()}, {@link #}
     * @param sport 
     * @see #getMatches() 
     * @see #getMatchesOfDay(java.util.Calendar) 
     * @see #nextDay() 
     * @see #previousDay() 
     */
    void setExploredSport(BettingSports sport);
    
    /**
     * Vráti aktuálne nastavený šport pre stiahnutie.
     * @return 
     * @see #setExploredSport(BettingSports) 
     */
    BettingSports getExploredSport();
    
    /**
     * Nastaví štartovný dátum, od ktorého sa budú sťahovať zápasy pri najbližšom
     * volaní {@link #getMatches()}, prípadne {@link #previousDay()}. Zavolanie
     * metódy {@link #nextDay()} môže zrušiť nastavenia vykonané touto metódou.
     * @param start 
     * @see #getMatches()
     * @see #previousDay()
     * @see #nextDay()
     */
    void setStartDate(Calendar start);
    
    /**
     * Nastaví koncový dátum, do ktorého sa budú sťahovať zápasy pri najbližsom
     * zavolaní {@link #getMatches()}, prípadne {@link #nextDay()}. Zavolanie 
     * metódy {@link #previousDay()} môže zrušiť nastavenia vykonané touto metódou.
     * @param end 
     * @see #getMatches()
     * @see #previousDay() 
     * @see #nextDay()
     */
    void setEndDate(Calendar end);
    
    /**
     * Vráti nestiahnuté zápasy pri poslednom volaní niektorej z metód
     * {@link #getMatches()}, {@link #getMatchesOfDay(java.util.Calendar)}, 
     * {@link #nextDay()}, {@link #previousDay()}.
     * @return 
     * @see #getMatches() 
     * @see #getMatchesOfDay(java.util.Calendar) 
     * @see #nextDay() 
     * @see #previousDay()
     */
    Collection<String> getUndownloadedMatches();
    
    /**
     * Vráti nestiahnuté športy a dni pri poslednom volaní niektorej z metód
     * {@link #getMatches()}, {@link #getMatchesOfDay(java.util.Calendar)}, 
     * {@link #nextDay()}, {@link #previousDay()}.
     * @return 
     * @see #getMatches() 
     * @see #getMatchesOfDay(java.util.Calendar) 
     * @see #nextDay() 
     * @see #previousDay()
     */
    Collection<Tuple<BettingSports, Calendar>> getUndownloadedSports();
    
    /**
     * Vráti neuložené zápasy pri poslednom volaní niektorej z metód
     * {@link #getMatches()}, {@link #getMatchesOfDay(java.util.Calendar)}, 
     * {@link #nextDay()}, {@link #previousDay()}.
     * @return 
     * @see #getMatches() 
     * @see #getMatchesOfDay(java.util.Calendar) 
     * @see #nextDay() 
     * @see #previousDay()
     */
    Collection<Match> getUnsavedMatches();
}
